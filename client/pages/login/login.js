import { getMineInfo } from '../../utils/api'
import { compareVersion } from '../../utils/util'

Page({
  data: {
  },

  onLogin() {
    console.log("onLogin")
    var that = this
    wx.showToast({
      title: '处理中',
      icon: 'loading',
      duration: 5000
    });

    getApp().getLoginInfo(loginInfo => {
      wx.switchTab({
        url: '/pages/index/index',
      })
    })

  },

  onLogin2(){
    
    wx.getSystemInfo({
      success: function (res) {
        if (compareVersion(res.SDKVersion, '1.3.0') < 0)
        {
          wx.showModal({
            title: '友情提示',
            content: '微信版本过低请升级后再使用',
          })   
        }
      }
    })
  }
})