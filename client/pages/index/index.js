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
    type: 0,
    banner_arr: [{
        banner_id: 1,
        carousel_img: '/images/tmp/banner1.png'
      },
      {
        banner_id: 2,
        carousel_img: '/images/tmp/banner2.png'
      },
      {
        banner_id: 3,
        carousel_img: '/images/tmp/banner3.png'
      }
    ],
    list: null
    /*
    list:[
      { task_id: 1, task_name: 'ETC推广服务', task_type: 'my_create', standard: '照片清晰，用户激活', salary: '15', end_date: '2019-11-23', next_step: '任务管理' },
      { task_id: 1, task_name: '小程序推广', task_type: '我参与的', standard: '用户完成注册登录', salary: '15', end_date: '2019-11-23', next_step: '数据采集' },
      { task_id: 1, task_name: 'ETC推广服务', task_type: '我参与的', standard: '照片清晰，用户激活', salary: '10', end_date: '2019-11-23', next_step: '数据采集' }
      
    ]*/
  },
  //下拉更新
  onPullDownRefresh: function() {

  },

  onShow: function() {
    console.log("index on show")
    this.initData()
    this.getMyTask(0)
  },
  onLoad: function() {
    if (wx.getStorageSync('haslogin') == true) {
      let userInfo = wx.getStorageSync('userInfo');
      this.setData({
        userInfo: userInfo,
        haslogin: true
      });

    }
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
    console.log("getMyTask:" + JSON.stringify(this.data.haslogin))

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
        that.setData({
          load:false
        })
      }
    })

  },

  findTask: function() {
    wx.switchTab({
      url: '/pages/task/task',
    })
  },

  callback() {
    this.initData()
    this.getMyTask()
  }

});