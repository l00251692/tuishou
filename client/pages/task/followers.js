// pages/task/followers.js
import {
  getFollowers, 
} from '../../utils/api'

import dateFormat from '../../utils/dateformat'

Page({

  /**
   * 页面的初始数据
   */
  data: {
    hasMore: true,
    loading: false,
    hidden:true,
    page: 0,
    followers_list:null
  },

  /**
   * 生命周期函数--监听页面加载
   */
  onLoad: function (options) {
    var id = options.id
    var start_date = options.start_date
    this.callback = options.callback || 'callback'
    var end_date = dateFormat(new Date(), "yyyy-mm-dd")
    this.setData({
      project_id: id,
      start_date,
      end_date
    })

    this.loadData()
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
    console.log("onshow")
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

  /**
   * 页面相关事件处理函数--监听用户下拉动作
   */
  onPullDownRefresh: function () {

  },

  /**
   * 页面上拉触底事件的处理函数
   */
  onReachBottom: function () {
    this.loadData();
  },

  loadData() {
    var that = this
    var { project_id, start_date, end_date, page, loading} = this.data
    console.log("loadData:" + JSON.stringify(this.data))

    if(loading)
    {
      return;
    }

    this.setData({
      loading:true
    })

    getFollowers({
      project_id,
      start_date,
      end_date,
      page,
      success(data){
        var list = data.list
        var { followers_list } = that.data
        that.setData({
          followers_list: followers_list ? followers_list.concat(list) : list,
          page: page + 1,
          hasMore: data.count == 10, //一次最多10个，如果这次取到10个说明还有
          loading: false
        })
  
      },
      error(res){
        that.setData({
          loading: false
        })
      }

    })
  },

  onChooseDate:function(e){
    var hidden = this.data.hidden

    this.setData({
      hidden:!hidden
    })
  },

  select(e) {
    console.log(JSON.stringify(e.detail))
    var detail = e.detail 
    var hidden = this.data.hidden
    this.setData({
      start_date: detail.begin.text,
      end_date: detail.over.text,
      hidden: !hidden,
      hasMore: true,
      page: 0,
      loading:false,
      followers_list:null
    });
    this.loadData();
  },

  cancel(){
    var hidden = this.data.hidden

    if (!hidden){
      this.setData({
        hidden: !hidden
      })
    }
  },

  /**
   * 用户点击右上角分享
   */
  onShareAppMessage: function () {

  }
})