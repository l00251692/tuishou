// pages/order/pay.js
const qiniuUploader = require("../../utils/qiniuUploader")
import {
  addOrder, getQiniuToken, uploadOrderIdCard, getPayment, updateOrderPayed
} from '../../utils/api'
import {
  alert, requestPayment, randomString
} from '../../utils/util'
Page({

  /**
   * 页面的初始数据
   */
  data: {
    canClick:true,
    agree: false
  },

  /**
   * 生命周期函数--监听页面加载
   */
  onLoad: function (options) {
    this.init()
  },

  onShow(){
    
  },

  init() {
    var info = wx.getStorageSync("info")
    var arr = ['男', '女']
    if (info) {
      this.setData({
        name: info.name,
        idcard: info.idcard,
        sex: arr[info.sexIndex],
        hospital: info.selectHospital,
        hospitalArea: info.hospitalArea,
        department: info.department,
        bedNo: info.bedNo,
        mrNo: info.mrNo,
        doctor: info.doctor,
        diseases: info.diseases,
        date: info.date,
        adDetail: info.adDetail,
        phone: info.phone,
        concatName: info.concatName,
        concatPhone: info.concatPhone,
      })
    }

    var address = wx.getStorageSync("address")
    var { adDetail } = this.data
    if (address) {
      this.setData({
        address: address,
        addressStr: address.province + address.city + address.title + adDetail
      })
    }

    var tmp1 = wx.getStorageSync('idCardFrontPath')
    if (tmp1) {
      this.setData({
        idCardFrontPath: tmp1
      })
    }

    var tmp2 = wx.getStorageSync('idCardBackPath')
    if (tmp2) {
      this.setData({
        idCardBackPath: tmp2
      })
    }

    var tmp3 = wx.getStorageSync('outSummaryPath')
    if (tmp3) {
      this.setData({
        outSummaryPath: tmp3
      })
    }
  },

  formSubmit(e) {
    var {
      agree, name, idcard, sex, hospital, hospitalArea, department, bedNo, mrNo, doctor, diseases, date, address, adDetail, phone, concatName, concatPhone, idCardFrontPath, idCardBackPath, outSummaryPath,sign
    } = this.data

    if (agree == false) {
      alert('请接受相关委托服务合同协议');
      return;
    }

    if (sign == null || sign.length == 0)
    {
      alert('请进行签名确认');
      return;
    }
    
    var that = this
    this.setData({
      canClick:false
    })

    wx.showLoading({
      title: '提交中...',
    })

    var addresstr = JSON.stringify(address)
    
    addOrder({
      name, idcard, sex, hospital, hospitalArea, department, bedNo, mrNo, doctor, diseases, date, phone, concatName, concatPhone, addresstr, adDetail,
      success(data) {
        var token = ''
        var order_id = data.orderId
        var key_str = randomString(4) //随机4位字符串用于保密
        getQiniuToken({
          success(data) {
            token = data.upToken;
            qiniuUploader.upload(idCardFrontPath, (res) => {
              var front_img = res.imageURL
              //上传反面身份证照片
              qiniuUploader.upload(idCardBackPath, (res) => {
                var back_img = res.imageURL
                //上传出院小结照片
                qiniuUploader.upload(outSummaryPath, (res) => {
                  var summary_img = res.imageURL
                  qiniuUploader.upload(sign, (res) => {
                    var sign_img = res.imageURL
                    //更新照片信息到订单信息中
                    uploadOrderIdCard({
                      front_img,
                      back_img,
                      summary_img,
                      sign_img,
                      order_id,
                      success(data) {
                        //获取支付参数
                        var pay_money = "200.00"
                        getPayment({
                          order_id,
                          pay_money,
                          success(data) {
                            wx.hideLoading()
                            //发起微信支付
                            console.log("getPayment success:")
                            requestPayment({
                              data,
                              success() {
                                //更新订单状态
                                updateOrderPayed({
                                  order_id,
                                  success(data) {
                                    wx.switchTab({
                                      url: '/pages/mine/mine',
                                    })
                                  }
                                })
                              },
                              error(data) {
                                console.log("用户取消支付")
                                that.setData({
                                  canClick: true
                                })
                                wx.switchTab({
                                  url: '/pages/mine/mine',
                                })
                              }
                            })

                          }, error(data) {
                            console.log("getPayment err:" + JSON.stringify(data))
                            wx.hideLoading()
                            that.setData({
                              canClick: true
                            })
                            alert("支付失败，请重试")
                          }
                        })
                      },
                      error(data) {
                        console.log("订单上传照片失败")
                        wx.hideLoading()
                        that.setData({
                          canClick: true
                        })
                        alert("订单上传照片失败")
                      }

                    })

                  }, (error) => {
                    console.log('error4: ' + error);
                    wx.hideLoading()
                    that.setData({
                      canClick: true
                    })
                    alert("上传签名照片失败")
                  }, {
                      region: 'ECN', //华东
                      domain: 'img.mingjing.tech',
                      key: 'order_' + order_id + '_' + key_str + '_sign',
                      uptoken: token
                    }, (res) => {
                    });

                }, (error) => {
                  console.log('error3: ' + error);
                  wx.hideLoading()
                  that.setData({
                    canClick: true
                  })
                  alert("上传出院小结照片失败")
                }, {
                    region: 'ECN', //华东
                    domain: 'img.mingjing.tech',
                    key: 'order_' + order_id + '_' + key_str + '_summary',
                    uptoken: token
                  }, (res) => {
                  });

              }, (error) => {
                console.log('error2: ' + error);
                wx.hideLoading()
                that.setData({
                  canClick: true
                })
                alert("上传身份证背面照片失败")  
              }, {
                  region: 'ECN', //华东
                  domain: 'img.mingjing.tech',
                  key: 'order_' + order_id + '_' + key_str + '_back',
                  uptoken: token
                }, (res) => {
                });

            }, (error) => {
              console.log('error: ' + error);
              wx.hideLoading()
              that.setData({
                canClick: true
              })
              alert("上传身份证正面照片失败")
              
            }, {
                region: 'ECN', //华东
                domain: 'img.mingjing.tech',
                key: 'order_' + order_id + '_' + key_str + '_front',
                uptoken: token
              }, (res) => {
                
              });
          },
          error(data) {
            wx.hideLoading()
            that.setData({
              canClick: true
            })
            alert("照片上传服务器信息获取失败，请稍后重试")
           
          }
        })
      },
      error(data) {
        console.log("订单创建失败，请稍后重试")
        wx.hideLoading()
        that.setData({
          canClick: true
        })
      }
    })
  },

  previewImage: function (e) {
    var { idCardFrontPath ,idCardBackPath} = this.data
    var imgalist = [idCardFrontPath, idCardBackPath]

    var current = e.target.dataset.src;
    wx.previewImage({
      current: current, // 当前显示图片的http链接
      urls: imgalist // 需要预览的图片http链接列表
    })
  },

  onSign(){
    var that = this
    wx.navigateTo({
      url: '/pages/handwriting/handwriting?callback=callback'
    })
  },

  callback(){
    var tmp = wx.getStorageSync('sign')
    if (tmp) {
      this.setData({
        sign: tmp
      })
    }
  },

  toAgree: function (e) {
    var { agree } = this.data
    this.setData({
      agree: !agree
    })
  },

  tapHelp: function (e) {
    if (e.target.id == 'help') {
      this.hideHelp();
    }
  },
  
  showHelp: function (e) {
    this.setData({
      'help_status': true
    });
  },

  hideHelp: function (e) {
    this.setData({
      'help_status': false
    });
  }

})