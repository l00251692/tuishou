
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
    isShow: false,//控制emoji表情是否显示
    isLoad: true,//解决初试加载时emoji动画执行一次
    content: "",//评论框的内容
    //isLoading: true,//是否显示加载数据提示
    disabled: true,
    cfBg: false,
    _index: 0,
    emojiChar: "😃-😋-😌-😍-😏-😜-😝-😞-😔-😪-😭-😁-😂-😃-😅-😆-👿-😒-😓-😔-😏-😖-😘-😚-😒-😡-😢-😣-😤-😢-😨-😳-😵-😷-😸-😻-😼-😽-😾-😿-🙊-🙋-🙏-✈-🚇-🚃-🚌-🍄-🍅-🍆-🍇-🍈-🍉-🍑-🍒-🍓-🐔-🐶-🐷-👦-👧-👱-👩-👰-👨-👲-👳-💃-💄-💅-💆-💇-🌹-💑-💓-💘-🚲",
    //0x1f---
    emoji: [
      "60a", "60b", "60c", "60d", "60f",
      "61b", "61d", "61e", "61f",
      "62a", "62c", "62e",
      "602", "603", "605", "606", "608",
      "612", "613", "614", "615", "616", "618", "619", "620", "621", "623", "624", "625", "627", "629", "633", "635", "637",
      "63a", "63b", "63c", "63d", "63e", "63f",
      "64a", "64b", "64f", "681",
      "68a", "68b", "68c",
      "344", "345", "346", "347", "348", "349", "351", "352", "353",
      "414", "415", "416",
      "466", "467", "468", "469", "470", "471", "472", "473",
      "483", "484", "485", "486", "487", "490", "491", "493", "498", "6b4"
    ],
    emojis: [],//qq、微信原始表情
    alipayEmoji: [],//支付宝表情
    title: ''//页面标题,
  },
  onLoad: function (options) {
    // 页面初始化 options为页面跳转所带来的参数
    this.id = options.id
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
    //var emojis = []
    //var em = {}
    //var emChar = that.data.emojiChar.split("-");
    /*
    that.data.emoji.forEach(function (v, i) {
      em = {
        char: emChar[i],
        emoji: "0x1f" + v
      };
      emojis.push(em)
    });
    
    that.setData({
      emojis: emojis
    })*/

    getProjectInfo(
      {
        project_id: id,
        success(data) {
          console.log(JSON.stringify(data))
          that.setData({
            info: data
          })
        }
      }
    )
  },


  onShareAppMessage() {
    var { info } = this.data
    return {
      title: info.item_title,
      path: '/pages/project/detail'
    }
  },

  onShare: function(e){

    var that = this;
    //1. 请求后端API生成小程序码
    wx.showToast({
      title: '图片生成中...',
      icon: 'loading',
      duration: 4000
    });
    getShareQr({
      project_id:that.id,
      success(data)
      {
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
                  url: that.data.info.project_head,
                  success: function (res) {
                    bgImgPath = res.tempFilePath
                  },
                  fail: function (err) {
                    console.log("download project_head fail")
                  },
                  complete: function (e) {
                    ctx.drawImage(bgImgPath, 0, 0, 600, 320)

                    ctx.setFillStyle('white')
                    ctx.fillRect(0, 320, 600, 280);

                    var title = that.data.info.item_title
                    if (title.length > 20) {
                      title = title.substring(0, 16) + "..."
                    }

                    ctx.setFontSize(30)
                    ctx.setFillStyle('#111111')
                    ctx.fillText(title, 30, 350, 570)

                    ctx.drawImage(userHead, 30, 410, 60, 60)
                    ctx.drawImage(qrPath, 410, 410, 160, 160) //二维码图片

                    ctx.setFontSize(28)
                    ctx.setFillStyle('#6F6F6F')
                    ctx.fillText(that.data.info.create_userName, 110, 450)

                    ctx.setFontSize(24)
                    ctx.fillText('长按扫码查看详情', 30, 570)

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
                height: 600,
                  destWidth: 600,
                  destHeight: 600,
                  canvasId: 'shareCanvas',
                  success: function (res) {
                    console.log(res.tempFilePath);
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
          fail:function(res){
            console.log("333333")
            console.log(JSON.stringify(res))
          }
        })
      }
    });
  },

  saveSharePic:function(e)
  {
    var that = this
    //4. 当用户点击分享到朋友圈时，将图片保存到相册
    console.log("saveSharePic:" + that.data.shareImgSrc)
    wx.saveImageToPhotosAlbum({
      filePath: that.data.shareImgSrc,
      success(res) {
        wx.showModal({
          title: '存图成功',
          content: '图片成功保存到相册了，可以分享到朋友圈了~',
          showCancel: false,
          confirmText: '好的',
          confirmColor: '#72B9C3',
          success: function (res) {
            if (res.confirm) {
              console.log('用户点击确定');
            }
            that.setData({
              hidden: true
            })
          }
        })
      }
    })
  },
  

  onFollow: function (e) {
    var{  info:{follow} } = this.data
    var that = this

    setProjectFollowStatus({
      status: !follow,
      project_id : that.id,
      success(data)
      {
        that.setData({
          'info.follow': !follow 
        })
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

    deleteProject({
      project_id: that.id,
      success(data) {
        wx.showToast({
          title: '删除成功',
          icon: 'none',
          duration: 1500
        });

        wx.switchTab({
          url: '/pages/index/index',
        })

      }
    })
  },

  jyHistory:function(){
    
    console.log("jyHistory:" + this.id)
    
    wx.navigateTo({
      url: '/pages/task/collects?id=' + this.id,
    })
  }

})