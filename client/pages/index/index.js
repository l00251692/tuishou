//index.js
import {
  getMyProjectList
} from '../../utils/api'

import {
  alert
} from '../../utils/util'
//获取应用实例
var app = getApp();
Page({
  data: {
    page: 0,
    hasMore: true,
    loading: false,
    type: 0,
    list: null
  },

  onLoad: function() {
    if (wx.getStorageSync('haslogin') == true) {
      let userInfo = wx.getStorageSync('userInfo');
      this.setData({
        userInfo: userInfo,
        haslogin: true
      });
      this.initData()
      this.getMyTask(0)
    }
  },

  onShow: function () {
    if (getApp().globalData.index_refresh == true) {
      this.initData()
      this.getMyTask(0)
      getApp().globalData.index_refresh = false
    }
  },

  onPullDownRefresh: function () {
    if (wx.getStorageSync('haslogin') == true){
      this.initData()
      this.getMyTask(0)
    }
  },

  onReachBottom: function () {
    this.getMyTask(0)
  },

  initData() {
    this.setData({
      page: 0,
      hasMore: true,
      loading: false,
      list: null
    })
  },

  getMyTask: function(type) {

    if (this.data.loading) {
      return;
    }
    var that = this;
    var { page } = this.data

    this.setData({
      loading: true
    })

    getMyProjectList({
      page,
      type,
      success(data) {
        var tmp = data.list
        var {  list } = that.data
        that.setData({
          list: list ? list.concat(tmp) : tmp,
          page: page + 1,
          hasMore: data.count == 10, //一次最多10个，如果这次取到10个说明还有
          loading: false
        })
      },
      error(res){
        wx.showToast({
		  icon: 'loading',
          title: '连接失败，请稍候再试',
        })
        that.setData({
          loading:false
        })
      }
    })

  },

  findTask: function() {
    wx.switchTab({
      url: '/pages/task/task',
    })
  },

  createTask: function () {
    var userInfo = wx.getStorageSync("userInfo")

    if (userInfo == null || userInfo.phone == null || userInfo.district == null || userInfo.phone.length == 0 || userInfo.district.length ==0) {
      wx.navigateTo({
        url: '/pages/mine/append',
      })
	  return
    }

    wx.navigateTo({
      url: '/pages/task/create',
    })
  },

  callback() {
    this.initData()
    this.getMyTask(0)
  },

  /**
   * 用户点击右上角分享
   */
  onShareAppMessage: function () {
    return {
      title: '推手号',
      path: '/pages/index/index'
    }
  }

});