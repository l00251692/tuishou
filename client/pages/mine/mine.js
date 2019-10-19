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