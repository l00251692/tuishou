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
    canClick:true 
  },

  /**
   * 生命周期函数--监听页面加载
   */
  onLoad: function (options) {
    this.init()
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
  },

  formSubmit(e) {
    var {
      name, idcard, sex, hospital, hospitalArea, department, bedNo, mrNo, doctor, diseases, date, address, adDetail, phone, concatName, concatPhone, idCardFrontPath, idCardBackPath
    } = this.data

    var that = this
    this.setData({
      canClick:false
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
                //更新照片信息到订单信息中
                uploadOrderIdCard({
                  front_img,
                  back_img,
                  order_id,
                  success(data) {
                    //获取支付参数
                    var pay_money = "200.00"
                    getPayment({
                      order_id,
                      pay_money,
                      success(data) {
                        //发起微信支付
                        console.log("getPayment success:")
                        requestPayment({
                          data,
                          success() {
                            //更新订单状态
                            updateOrderPayed({
                              order_id,
                              success(data){
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
                          }
                        })
                        
                      }, error(data) {
                        console.log("getPayment err:" + JSON.stringify(data))
                        that.setData({
                          canClick: true
                        })
                      }
                    })
                  },
                  error(data) {
                    console.log("订单上传照片失败")
                    that.setData({
                      canClick: true
                    })
                  }

                })

              }, (error) => {
                console.log('error2: ' + error);
                that.setData({
                  canClick: true
                })
                alert("上传身份证照片失败")  
              }, {
                  region: 'ECN', //华东
                  domain: 'img.mingjing.tech',
                  key: 'order_' + order_id + '_' + key_str + '_back',
                  uptoken: token
                }, (res) => {
                });

            }, (error) => {
              console.log('error: ' + error);
              that.setData({
                canClick: true
              })
              alert("上传身份证照片失败")
              
            }, {
                region: 'ECN', //华东
                domain: 'img.mingjing.tech',
                key: 'order_' + order_id + '_' + key_str + '_front',
                uptoken: token
              }, (res) => {
                
              });
          },
          error(data) {
            that.setData({
              canClick: true
            })
            alert("照片上传服务器信息获取失败，请稍候")
           
          }
        })
      },
      error(data) {
        console.log("订单创建失败，请稍后重试")
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