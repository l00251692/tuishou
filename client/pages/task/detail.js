
import {
  alert,getPrevPage
} from '../../utils/util'

import {
  getProjectInfo, getShareQr, setProjectFollowStatus, deleteProject
} from '../../utils/api'

Page({
  data: {
    art: {},
    info:{},
    /*
    info:{task_head: '/images/tmp/task1.jpg', task_name:'ETC推广', region:'全国', start_date:'2019-09-23', remain_days:5, salary:'￥15元/单', detail:'在全国小区内进行ETC推广，每单衔接，需要上传推广照片，照片清晰以及用户完成办理' },*/
    tabs: ["项目详情"],
    activeIndex: 0,
    hasMore: true,
    loading: false,
    page: 0,
    hidden:true, //控制分享生成的图片是否显示
    title: ''//页面标题,
  },
  onLoad: function (options) {
    if (options.scene) {
      console.log("has scene");
      var scene = decodeURIComponent(options.scene);
      console.log("scene is ", scene);
      var arrPara = scene.split("&");
      var arr = [];
      for (var i in arrPara) {
        arr = arrPara[i].split("=");
        console.log("log:", arr[0], "=", arr[1]);
        if (arr[0] == 'id')
        {
          this.id = arr[1]
        }
      }
    } else {
      console.log("no scene");
      this.id = options.id
    }
 
    // 页面初始化 options为页面跳转所带来的参数
    this.callback = options.callback || 'callback'
    this.loadData()
    //this.loadReview()   
  },
  onReady: function () {
    // 页面渲染完成
  },
  onShow: function () {
    // 页面显示
  },
  onHide: function () {
    // 页面隐藏
  },
  onUnload: function () {
    // 页面关闭
  },

  loadData() {
    var that = this
    var id = this.id;
    console.log("loadData:" + id)
    
    var loading = this.data.loading
    if(loading){
      return
    }

    this.setData({
      loading:true
    })

    getProjectInfo(
      {
        project_id: id,
        success(data) {
          console.log(JSON.stringify(data))
          that.setData({
            info: data,
            loading:false
          })
        },
        error(res){
          that.setData({
            loading: false
          })
        }
      }
    )
  },


  onShareAppMessage() {
    var { info } = this.data
    return {
      title: info.item_title,
      path: '/pages/project/detail?id=' + info.task_id
    }
  },

  onFollow: function (e) {
    var{  info:{follow, start_date} } = this.data
    var that = this

    var userInfo = wx.getStorageSync("userInfo")

    if (userInfo == null || userInfo.phone == null || userInfo.city == null || userInfo.phone.length == 0 || userInfo.city.length == 0) {
      return alert("请前往“我的”--“完善信息”进行授权，以便平台更好为您服务")
    }
    
    setProjectFollowStatus({
      status: !follow,
      project_id : that.id,
      success(data)
      {
        that.setData({
          'info.follow': !follow 
        })
        //getPrevPage()[that.callback]()
        getApp().globalData.index_refresh = true
        wx.showToast({
          title: !follow ? '参与成功' : '取消成功',
          icon: 'none',
          duration: 1500
        });
        
      }
    })     
  },

  onDelete: function (e) {
    var that = this
    var  { followers, collects } = this.data.info
    if(followers !=0 || collects !=0 )
    {
      alert("当前任务有用户参与或上传数据，请联系管理员确认删除")
      return
    }

    deleteProject({
      project_id: that.id,
      success(data) {
        wx.showToast({
          title: '删除成功',
          icon: 'none',
          duration: 1500
        });
        getApp().globalData.index_refresh = true
        getApp().globalData.task_refresh = true

        wx.switchTab({
          url: '/pages/index/index',
        })

      }
    })
  },

  followersManager: function () {

    console.log("followersManager:" + this.id)
    var { info } = this.data

    wx.navigateTo({
      url: '/pages/task/followers?id=' + this.id + '&&start_date=' + info.start_date,
    })
  },

  collectManager:function(){
    
    console.log("jyHistory:" + this.id)
    
    wx.navigateTo({
      url: '/pages/task/collects?id=' + this.id,
    })
  },

  onShare: function (e) {

    var that = this;
    //1. 请求后端API生成小程序码
    wx.showToast({
      title: '图片生成中...',
      icon: 'loading',
      duration: 8000
    });

    getShareQr({
      project_id: that.id,
      success(data) {
        that.setData({
          qrImgPath: data.path
        })
        var { qrImgPath } = that.data
        wx.getImageInfo({
          src: qrImgPath,
          success: function (res) {
            //2. canvas绘制文字和图片
            const ctx = wx.createCanvasContext('shareCanvas')
            var qrPath = res.path
            var userHead = 'images/default_useHead.png'
            var bgImgPath = 'images/index/default-project-head.png'

            wx.downloadFile({
              url: that.data.info.create_userHead,
              success: function (res) {
                userHead = res.tempFilePath
              },
              fail: function (err) {
                console.log("download userHead fail")
              },
              complete: function (e) {
                wx.downloadFile({
                  url: that.data.info.task_head,
                  success: function (res) {
                    bgImgPath = res.tempFilePath
                  },
                  fail: function (err) {
                    console.log("download project_head fail")
                  },
                  complete: function (e) {
                    ctx.drawImage(bgImgPath, 0, 0, 600, 320)

                    ctx.setFillStyle('white')
                    ctx.fillRect(0, 320, 600, 480);

                    var title = that.data.info.task_name
                    if (title.length > 20) {
                      title = title.substring(0, 16) + "..."
                    }

                    ctx.setFontSize(30)
                    ctx.setFillStyle('#111111')
                    ctx.fillText(title, 30, 350, 570)

                    ctx.drawImage(userHead, 30, 410, 60, 60)
                    ctx.drawImage(qrPath, 370, 500, 200, 200) //二维码图片

                    ctx.setFontSize(26)
                    ctx.setFillStyle('#6F6F6F')

                    var create_userName = that.data.info.create_userName
      
                    ctx.fillText(create_userName, 100, 450, 270)
                    ctx.setFontSize(24)

                    ctx.fillText("推广区域: " + that.data.info.region, 30, 510, 300)

                    ctx.fillText("结束日期: " + that.data.info.end_date, 30, 570, 300)
                    ctx.fillText("联系方式: " + that.data.info.contact, 30, 630, 300)

                    
                    ctx.fillText('长按扫码查看详情', 340, 770)

                    console.log("121212")
                    ctx.draw()
                  }
                })
              }
            })


            // 3. canvas画布转成图片
            setTimeout(function () {
              wx.canvasToTempFilePath({
                x: 0,
                y: 0,
                width: 600,
                height: 800,
                destWidth: 600,
                destHeight: 800,
                canvasId: 'shareCanvas',
                success: function (res) {
                  console.log(res.tempFilePath);
                  wx.hideToast()
                  that.setData({
                    shareImgSrc: res.tempFilePath,
                    hidden: false
                  })
                },
                fail: function (res) {
                  console.log(res)
                }
              })
            }, 2000)
          },
          fail: function (res) {
            console.log("333333")
            console.log(JSON.stringify(res))
          }
        })
      }
    });
  },

  saveSharePic: function (e) {
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
          content: '图片成功保存到相册了，可以分享到朋友圈了~',
          showCancel: false,
          confirmText: '好的',
          confirmColor: '#1e8cd4',
          success: function (res) {
            if (res.confirm) {
              console.log('用户点击确定');
            }
            that.setData({
              hidden: true
            })
          }
        })
      },
      fail(res){
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
