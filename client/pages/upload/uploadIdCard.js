// pages/upload/uploadIdCard.js
import {
  alert
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
    this.initData()
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

  initData(){

    var that = this
    var tmp1 = wx.getStorageSync('idCardFrontPath')
    if (tmp1){
      wx.getFileInfo({
        filePath: tmp1,
        success(res){
          that.setData({
            idCardFrontPath: tmp1
          })
        },
        fail(res){
          that.setData({
            idCardFrontPath: null
          })
        }
      })
      
    }

    var tmp2 = wx.getStorageSync('idCardBackPath')
    if (tmp2) {
      wx.getFileInfo({
        filePath: tmp2,
        success(res) {
          that.setData({
            idCardBackPath: tmp2
          })
        },
        fail(res) {
          that.setData({
            idCardBackPath: null
          })
        }
      })
    }

    var tmp3 = wx.getStorageSync('outSummaryPath')
    if (tmp3) {
      wx.getFileInfo({
        filePath: tmp3,
        success(res) {
          that.setData({
            outSummaryPath: tmp3
          })
        },
        fail(res) {
          that.setData({
            outSummaryPath: null
          })
        }
      })
    }
  },

  onChooseFront(e){
    wx.navigateTo({
      url: '/pages/upload/takePhoto?type=1&callback=callback',
    })
  },

  onChooseBack(e) {
    wx.navigateTo({
      url: '/pages/upload/takePhoto?type=2&callback=callback',
    })
  },

  onPhoto() {
    var that = this;
    wx.chooseImage({
      count: 1, // 默认9 
      sizeType: ['original'], // 可以指定是原图还是压缩图，默认二者都有 
      sourceType: ['camera', 'album' ], // 可以指定来源是相册还是相机，默认二者都有 
      success: function (res) {
        that.setData({
          outSummaryPath: res.tempFilePaths[0]
        })
        wx.setStorageSync("outSummaryPath", res.tempFilePaths[0])
      }
    })
  },

  uploadIdCard(e){

    var { idCardFrontPath, idCardBackPath, outSummaryPath} = this.data

    if (idCardFrontPath == null) {
      alert("请上传身份证正面照片")
      return
    }

    if (idCardBackPath == null) {
      alert("请上传身份证反面照片")
      return
    }

    if (outSummaryPath == null) {
      alert("请上传出院小结照片")
      return
    }

    wx.navigateTo({
      url: '/pages/order/pay',
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
  
  },

  callback(e){
    this.initData();
  }
})