//index.js
import {
  getMyProjectList
} from '../../utils/api'
//获取应用实例
var app = getApp();
Page({
  data: {
    page: 0,
    hasMore: true,
    loading: false,
    list: null
  },


  onShow: function() {

  },
  onLoad: function(options) {
    var type = options.type
    this.setData({
      type
    })

    if (wx.getStorageSync('haslogin') != true) {
      return
    }

    let userInfo = wx.getStorageSync('userInfo');
    this.setData({
      userInfo: userInfo,
      haslogin: true
    });
    this.initData()
    this.getMyTask(type)
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
    if (this.data.haslogin) {
      if (this.data.loading) {
        return;
      }
      var that = this;
      var {
        page
      } = this.data

      this.setData({
        loading: true
      })

      getMyProjectList({
        page,
        type,
        success(data) {
          var tmp = data.list
          var {
            list
          } = that.data
          that.setData({
            list: list ? list.concat(tmp) : tmp,
            page: page + 1,
            hasMore: data.count == 10, //一次最多10个，如果这次取到10个说明还有
            loading: false
          })
        }
      })
    } else {
      wx.showToast({
		icon: 'loading',
        title: '用户未登录，请先登录',
      })
    }
  },

  //下拉更新
  onPullDownRefresh: function () {
    if (wx.getStorageSync('haslogin') == true) {
      this.initData()
      this.getMyTask(this.data.type)
    }
  },

  onReachBottom: function () {
    this.getMyTask(this.data.type)
  }

});