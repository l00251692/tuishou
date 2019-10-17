import { getLoginInfo } from '../../utils/api'
import { compareVersion } from '../../utils/util'

const app = getApp()

Page({

  /**
   * 页面的初始数据
   */
  data: {

  },

  /**
   * 生命周期函数--监听页面加载
   */
  onLoad: function (options) {
    var haslogin = wx.getStorageSync("haslogin")
    if (haslogin == true) {
      wx.switchTab({
        url: '/pages/index/index',
      })
    }
    else {
      console.log("用户第一次使用")
    }
  },

  onLogin(e) {
    console.log("to login")
    console.log(e.detail.errMsg)
    if (e.detail.errMsg == 'getUserInfo:ok') {
      wx.switchTab({
        url: '/pages/index/index',
      })
      
      getApp().getLoginInfo(loginInfo => {
        wx.switchTab({
          url: '/pages/index/index',
        })
      })
    }
  }
})