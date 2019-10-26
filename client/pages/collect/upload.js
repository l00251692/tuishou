// pages/order/pay.js
const qiniuUploader = require("../../utils/qiniuUploader")
import dateFormat from '../../utils/dateformat'
import {
  getProjectInfo,
  getTuiguangQr,
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
    hidden: true,
    content: '',
    files: [],
    fileTimes: []
  },

  /**
   * 生命周期函数--监听页面加载
   */
  onLoad: function(options) {
    this.id = options.id
    this.callback = options.callback || 'callback'
    this.start_date = options.start_date
    console.log(options.start_date)
    if (wx.getStorageSync('haslogin') == true) {
      let userInfo = wx.getStorageSync('userInfo');
      this.setData({
        userInfo: userInfo,
        haslogin: true
      });
    }
    this.loadData()

  },

  loadData() {
    var that = this
    var id = this.id;
    console.log("loadData:" + id)

    var loading = this.data.loading
    if (loading) {
      return
    }

    this.setData({
      loading: true
    })

    getProjectInfo({
      project_id: id,
      success(data) {
        console.log(JSON.stringify(data))
        that.setData({
          info: data,
          loading: false
        })
      },
      error(res) {
        that.setData({
          loading: false
        })
      }
    })
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
            ctx.fillText(time, 10, ress.height - 10)
            ctx.strokeText(time, 10, ress.height - 10)

            ctx.draw(false, function() {
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
          fail: (e) => {
            console.log(e)
            alert("图片上传错误")
          }
        })

      }
    })
  },


  onUpload: function(res) {

    var start_date = this.start_date
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

    if (new Date(start_date) > new Date()) {
      wx.showModal({
        title: '提示',
        content: '当前任务还未开始，提交数据存在不会被审核风险',
        showCancel: true,
        confirmText: '继续',
        confirmColor: '#1e8cd4',
        success: function(res) {
          if (res.confirm) {
            console.log('用户点击确定');
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
                        key: 'prj_' + project_id + 'collect_' + collect_id + '_' + filePath_tmp.substr(30, 50),
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
                        success: function() {
                          setTimeout(function() {
                            wx.hideToast()
                            wx.navigateTo({
                              url: '/pages/collect/detail?id=' + collect_id,
                            })
                          }, 5000)

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
          } else if (res.cancel) {
            return
          }

        }
      })
    } else {
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
                  key: 'prj_' + project_id + 'collect_' + collect_id + '_' + filePath_tmp.substr(30, 50),
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
                  success: function() {
                    setTimeout(function() {
                      wx.hideToast()
                      wx.navigateTo({
                        url: '/pages/collect/detail?id=' + collect_id,
                      })
                    }, 5000)

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
    }

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


  onShare: function(e) {

    var that = this;
    //1. 请求后端API生成小程序码
    wx.showToast({
      title: '图片生成中...',
      icon: 'loading',
      duration: 8000
    });

    var {
      user_id,
      avatarUrl,
      nickName
    } = wx.getStorageSync("userInfo")

    getTuiguangQr({
      project_id: that.id,
      success(data) {
        that.setData({
          qrImgPath: data.path
        })
        var {
          qrImgPath
        } = that.data
        wx.getImageInfo({
          src: qrImgPath,
          success: function(res) {
            //2. canvas绘制文字和图片
            const ctx = wx.createCanvasContext('shareCanvas')
            var qrPath = res.path
            var userHead = 'images/default_useHead.png'
            var bgImgPath = 'images/index/default-project-head.png'

            wx.downloadFile({
              url: avatarUrl,
              success: function(res) {
                userHead = res.tempFilePath
              },
              fail: function(err) {
                console.log("download userHead fail")
              },
              complete: function(e) {
                wx.downloadFile({
                  url: that.data.info.task_head,
                  success: function(res) {
                    bgImgPath = res.tempFilePath
                  },
                  fail: function(err) {
                    console.log("download project_head fail")
                  },
                  complete: function(e) {
                    ctx.drawImage(bgImgPath, 0, 0, 600, 320)

                    ctx.drawImage(userHead, 225, 60, 150, 150)
                    ctx.setFontSize(40)
                    ctx.setFillStyle('#111111')
                    ctx.setTextAlign('center')
                    ctx.fillText(nickName, 300, 260)

                    //画下半部分
                    ctx.setFillStyle('white')
                    ctx.fillRect(0, 320, 600, 480);

                    //var title = that.data.info.task_name
                    //if (title.length > 20) {
                    //  title = title.substring(0, 16) + "..."
                    //}
                    var title = "扫一扫，有惊喜"

                    ctx.setFontSize(70)
                    ctx.setFillStyle('#1e8cd4')
                    ctx.setTextAlign('center')
                    ctx.fillText(title, 300, 400, 570)

                    ctx.drawImage(qrPath, 150, 470, 300, 300) //二维码图片

                    ctx.setFontSize(24)
                    ctx.setFillStyle('#6F6F6F')
                    ctx.setTextAlign('center')

                    ctx.fillText("任务ID: " + that.data.info.task_id, 300, 780, 300)
                    console.log("121212")
                    ctx.draw()
                  }
                })
              }
            })


            // 3. canvas画布转成图片
            setTimeout(function() {
              wx.canvasToTempFilePath({
                x: 0,
                y: 0,
                width: 600,
                height: 800,
                destWidth: 600,
                destHeight: 800,
                canvasId: 'shareCanvas',
                success: function(res) {
                  console.log(res.tempFilePath);
                  wx.hideToast()
                  that.setData({
                    shareImgSrc: res.tempFilePath,
                    hidden: false
                  })
                },
                fail: function(res) {
                  console.log(res)
                  wx.showToast({
                    title: '生成二维码失败，请联系客服',
                  })
                }
              })
            }, 2000)
          },
          fail: function(res) {
            console.log("333333")
            console.log(JSON.stringify(res))
          }
        })
      }
    });
  },

  saveSharePic: function(e) {
    var that = this
    //4. 当用户点击分享到朋友圈时，将图片保存到相册
    console.log("saveSharePic:" + that.data.shareImgSrc)
    wx.showToast({
      title: '图片保存中中...',
      icon: 'loading',
      duration: 2000
    });
    wx.saveImageToPhotosAlbum({
      filePath: that.data.shareImgSrc,
      success(res) {
        wx.showModal({
          title: '存图成功',
          content: '图片成功保存到相册了，快去推广吧~',
          showCancel: false,
          confirmText: '好的',
          confirmColor: '#1e8cd4',
          success: function(res) {
            if (res.confirm) {
              console.log('用户点击确定');
            }
            that.setData({
              hidden: true
            })
          }
        })
      },
      fail(res) {
        console.log("save fail:" + JSON.stringify(res))
        wx.showToast({
          title: '图片保存失败' + res,
          icon: 'loading',
          duration: 1000
        });
      }
    })
  },

})