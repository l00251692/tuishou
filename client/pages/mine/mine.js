import { bindPhone } from '../../utils/api'
import { alert } from '../../utils/util'

var app = getApp();

Page({
  data: {
    order: {
      unpaid: 0,
      unship: 0,
      unrecv: 0,
      uncomment: 0
    },
    haslogin: false,
    phone:"未绑定"
  },
  onLoad: function(options) {
    // 页面初始化 options为页面跳转所带来的参数
    //获取用户的登录信息
    if (wx.getStorageSync('haslogin'))
    {
      this.setData({
        haslogin:true,
        userInfo:wx.getStorageSync('userInfo')
      })
    }
    var that = this
    getApp().getLoginInfo(loginInfo => {
      console.log("login")
      that.setData({
        userInfo: loginInfo.userInfo,
        haslogin: true,
        phone: loginInfo.userInfo.phone,
        balance: loginInfo.userInfo.balance
      });
    })
    
  },
  onReady: function() {

  },
  onShow: function() {

  },
  onHide: function() {
    // 页面隐藏

  },
  onUnload: function() {
    // 页面关闭
  },

  
  onLogin(e) {

    if(this.data.haslogin == true)
    {
      return
    }

    if (e.detail.errMsg == 'getUserInfo:ok') {
      wx.showToast({
        title: '登录中...',
      })

      getApp().getLoginInfo(loginInfo => {
       console.log("login success")
      })
    }
  },

  getMyFollow(e){
    wx.navigateTo({
      url: '/pages/mine/mytask?type=2',
    })
  },

  getMyCreate(e) {
    wx.navigateTo({
      url: '/pages/mine/mytask?type=1',
    })
  },
  
  
  getOrderSummary(){
    var that = this
    console.log("that.data.userInfo.userId=" + that.data.userInfo.userId)
    util.request(api.OrderSummary, {
      userId: that.data.userInfo.userId
    }, 'POST').then(function (res) {
      if (res.status === 0) {
        that.setData({
          unpaid: res.data.unpaid,
          unship: res.data.unship,
          unrecv: res.data.unrecv,
          uncomment: res.data.uncomment
        });

      }
      else {
        console.log(res.message)
      }
    });
  },

  goOrder() {
    if (this.data.hasLogin) {
      wx.navigateTo({
        url: "/pages/ucenter/order/order"
      });
    } else {
      wx.showModal({
        title: '提示',
        content: '请登录后再查看',
      })
    }
  },
  goOrderIndex(e) {
    if (this.data.hasLogin) {
      let tab = e.currentTarget.dataset.index
      let route = e.currentTarget.dataset.route
      try {
        wx.setStorageSync('tab', tab);
      } catch (e) {

      }
      wx.navigateTo({
        url: route,
        success: function(res) {},
        fail: function(res) {},
        complete: function(res) {},
      })
    } else {
      wx.navigateTo({
        url: "/pages/auth/login/login"
      });
    };
  },

  bindPhoneNumber: function(e) {
    console.log("bind phone" + JSON.stringify(e) )
    if (e.detail.errMsg !== "getPhoneNumber:ok") {
      // 拒绝授权
      return;
    }
    
    var that = this
    var ency = e.detail.encryptedData;
    var iv = e.detail.iv;
    getApp().getLoginInfo(loginInfo => {
      var { session_key } = wx.getStorageSync('userInfo');

      bindPhone({
        ency,
        iv,
        session_key,
        success(data){
          wx.showToast({
            title: '绑定手机号成功',
          })
          that.setData({
            phone:data.phone
          })
        },
        error(res){
          alert("绑定手机号失败")
        }
      })
    })
  
  },
  
  exitLogin: function() {
    wx.showModal({
      title: '',
      confirmColor: '#b4282d',
      content: '退出登录？',
      success: function(res) {
        if (!res.confirm) {
          return;
        }

        util.request(api.AuthLogout, {}, 'POST');
        app.globalData.hasLogin = false;
        wx.removeStorageSync('token');
        wx.removeStorageSync('userInfo');
        wx.reLaunch({
          url: '/pages/index/index'
        });
      }
    })

  }
})