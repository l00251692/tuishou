// pages/upload/takePhoto.js
import {
  alert,getPrevPage
} from '../../utils/util'

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
    var that = this
    this.type = options.type
    this.callback = options.callback || 'callback'
    this.ctx = wx.createCameraContext()
  
  },

  /**
   * 生命周期函数--监听页面初次渲染完成
   */
  onReady: function () {
  
  },

  /**
   * 生命周期函数--监听页面显示
   */
  onShow: function () {
    var that = this
    console.log("takephoto onShow")
    wx.getSetting({
      success(res) {
        console.log(JSON.stringify(res))
        if (!res.authSetting['scope.camera']) {
          that.setData({
            authed: false
          })
        }
        else{
          that.setData({
            authed: true
          })
        }
      }
    })
  
  },

  /**
   * 生命周期函数--监听页面隐藏
   */
  onHide: function () {
  
  },

  /**
   * 生命周期函数--监听页面卸载
   */
  onUnload: function () {
  
  },

  openSetting(e){
    wx.navigateBack({})
  },

  bindCameraFail(e){
    this.setData({
      authed: false
    })
  },

  /**
   * 拍照
   */
  takePhoto: function () {
    var type = this.type
    var that = this
    that.ctx.takePhoto({
      quality: 'high',
      success: (res) => {
        if (type == 1)
        {
          wx.setStorageSync('idCardFrontPath', res.tempImagePath)
          console.log("拍摄照片成功")
        }
        else{
          wx.setStorageSync('idCardBackPath', res.tempImagePath)
          console.log("拍摄照片成功")
        }
        getPrevPage()[that.callback]()
        wx.navigateBack()          
      },
      fail: (res) => {
        alert("拍摄照片失败，请联系客服人员")
      }
    })
  },

  /**
   * 页面相关事件处理函数--监听用户下拉动作
   */
  onPullDownRefresh: function () {
  
  },

  /**
   * 页面上拉触底事件的处理函数
   */
  onReachBottom: function () {
  
  },

  /**
   * 用户点击右上角分享
   */
  onShareAppMessage: function () {
  
  }
})