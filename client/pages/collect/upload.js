// pages/order/pay.js
const qiniuUploader = require("../../utils/qiniuUploader")
import dateFormat from '../../utils/dateformat'
import {
  getQiniuToken,
  uploadCollectData,
  uploadCollectFile
} from '../../utils/api'

import {
  alert,
  getPrevPage,
  requestPayment,
  randomString
} from '../../utils/util'
Page({

  /**
   * 页面的初始数据
   */
  data: {
    loading: false,
    canClick: true,
    content: '',
    files: [],
    fileTimes:[]
  },

  /**
   * 生命周期函数--监听页面加载
   */
  onLoad: function(options) {
    this.id = options.id
    this.callback = options.callback || 'callback'
    if (wx.getStorageSync('haslogin') == true) {
      let userInfo = wx.getStorageSync('userInfo');
      this.setData({
        userInfo: userInfo,
        haslogin: true
      });

    }

  },

  listenerName: function(e) {
    this.setData({
      name: e.detail.value
    });
  },

  listenerPhone: function(e) {
    this.setData({
      phone: e.detail.value
    });
  },

  listenerOther: function(e) {
    this.setData({
      content: e.detail.value
    });
  },

  chooseImage: function(e) {
    if (this.data.files.length >= 5) {
      alert('只能上传五张图片')
      return false;
    }

    var that = this;
    var nickName = this.data.userInfo.nickName
    wx.chooseImage({
      count: 1,
      sizeType: ['original', 'compressed'],
      sourceType: ['camera'],
      success: function(res) {
        console.log("chooseImage:" + JSON.stringify(res))
        var time = dateFormat(new Date(), "yyyy-mm-dd HH:MM") 
        var originPath = res.tempFilePaths[0]
        //获取图片详细信息
        wx.getImageInfo({
          src: originPath,
          success: (ress) => {
            let ctx = wx.createCanvasContext('firstCanvas')

            that.setData({
              canvasHeight: ress.height,
              canvasWidth: ress.width
            })

            //将图片src放到cancas内，宽高为图片大小
            ctx.drawImage(originPath, 0, 0, ress.width, ress.height)
            //将声明的时间放入canvas
            ctx.setFontSize(15) //注意：设置文字大小必须放在填充文字之前，否则不生效
            ctx.setFillStyle('blue')
            ctx.fillText("推手号", 10, ress.height - 50)
            ctx.strokeText("推手号", 10, ress.height - 50)
            ctx.fillText(nickName, 10, ress.height - 30)
            ctx.strokeText(nickName, 10, ress.height - 30)
            ctx.fillText(time, 10, ress.height-10)
            ctx.strokeText(time, 10, ress.height-10)

            ctx.draw(false, function () {
              wx.canvasToTempFilePath({
                canvasId: 'firstCanvas',
                success: (res) => {
                  console.log("save cavas to temp path" + res.tempFilePath)
                  that.setData({
                    files: that.data.files.concat(res.tempFilePath),
                    fileTimes: that.data.fileTimes.concat(time)
                  });
                },
                fail: (e) => {
                  console.log(e)
                  that.setData({
                    files: that.data.files.concat(originPath),
                    fileTimes: that.data.fileTimes.concat(time)
                  });
                }
              })
            })
          },
          fail:(e) =>{
            console.log(e)
            alert("图片上传错误")
          }
        })

      }
    })
  },


  onUpload: function(res) {

    var that = this
    var {
      name,
      phone,
      content,
      files
    } = this.data
    var project_id = this.id

    this.setData({
      loading: true
    })

    if (phone == null) {
      return alert('请输入联系电话')
    }

    if (files.length == 0) {
      return alert('请上传照片信息')
    }

    uploadCollectData({
      project_id,
      name,
      phone,
      content,
      success(data) {
        //获取上传七牛云的token
        var token = ''
        var collect_id = data.collectId
        getQiniuToken({
          success(data) {
            token = data.upToken;
            //逐张内容图片上传
            var fail_num = 0;
            for (var i = 0; i < files.length; i++) {
              var filePath_tmp = files[i]
              console.log("url:" + filePath_tmp)
              qiniuUploader.upload(filePath_tmp, (res) => {
                //成功后将地址更新到服务器
                console.log("res.imageURL:" + res.imageURL)
                uploadCollectFile({
                  collect_id,
                  file: res.imageURL,
                })

              }, (error) => {
                fail_num++
                console.log('error: ' + error);
              }, {
                region: 'ECN', //华东
                domain: 'pz5gehtkk.bkt.clouddn.com', //
                  key: 'prj_' + project_id + 'collect_' + collect_id + '_'  + filePath_tmp.substr(30, 50),
                uptoken: token
              }, (res) => {
                console.log('上传进度', res.progress)
                console.log('已经上传的数据长度', res.totalBytesSent)
                console.log('预期需要上传的数据总长度', res.totalBytesExpectedToSend)
              });
            }

            if (fail_num == 0) {
              that.setData({
                loading: false
              })

              wx.showToast({
                title: '提交数据中...',
                duration: 5000,
                success: function(){
                  setTimeout(function () {
                    wx.hideToast()
                    wx.navigateTo({
                      url: '/pages/collect/detail?id=' + collect_id,
                    })
                  }, 4000)
                  
                } 
              })
            }
          },
          error(res) {
            console.log("get Qiniu token fail")
            that.setData({
              loading: false
            })
          }
        })

      },
      error(data) {
        alert('提交数据失败，请稍后')
        that.setData({
          loading: false
        })
      }
    })


  },
  previewImage: function(e) {
    wx.previewImage({
      current: e.currentTarget.id, // 当前显示图片的http链接
      urls: this.data.files // 需要预览的图片http链接列表
    })
  },

  deleteImg: function(e) {
    var imgs = this.data.files
    var times = this.data.fileTimes
    imgs.splice(e.currentTarget.dataset.index, 1)
    times.splice(e.currentTarget.dataset.index, 1)
    this.setData({
      files: imgs,
      fileTimes: times
    })
  },

})