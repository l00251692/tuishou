// pages/order/detail.js

import {
  getCollectInfo, getPayment, updateOrderPayed, cancelCollect, passCollect, rejectCollect
} from '../../utils/api'

import {
  alert, requestPayment, getPrevPage
} from '../../utils/util'

Page({

  /**
   * 页面的初始数据
   */
  data: {
   canClick:true
  },

  /**
   * 生命周期函数--监听页面加载
   */
  onLoad: function (options) {
    this.id = options.id
    this.callback = options.callback || 'callback'
    this.init()
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

  init(){
    var that = this
    var collect_id = this.id

    getCollectInfo({
      collect_id,
      success(data){
        that.setData({
          info:data.info,
          state: data.state
        })
      },
      error(){
        wx.showToast({
          icon: 'loading',
          title: '查看详情失败',
        })
      }
    })
  },

  previewImage: function (e) {
    wx.previewImage({
      current: e.currentTarget.id, // 当前显示图片的http链接
      urls: this.data.info.files // 需要预览的图片http链接列表
    })
  },

  detailImgInfo: function (e) {
    console.log("detailImg:" + e.currentTarget.dataset.index)
    var imgs = this.data.info.files
    var img = imgs[e.currentTarget.dataset.index]

  },

  onPass: function (e) {
    var collect_id = this.id
    var that = this

    passCollect({
      collect_id,
      success(data) {
        that.init()
        getPrevPage()[that.callback]()
      }
    })
  },

  onReject: function (e) {
    var collect_id = this.id
    var that = this

    wx.showActionSheet({
      itemList: ['采集信息错误', '采集照片模糊'],
      success(res) {
        console.log(res.tapIndex)
        var reason
        if (res.tapIndex ==0)
        {
          reason = '采集信息错误'
        }
        else{
          reason = '采集照片模糊'
        }
        rejectCollect({
          collect_id,
          reason,
          success(data) {
            that.init()
            getPrevPage()[that.callback]()
          }
        })
      },
      fail(res) {
        console.log(res.errMsg)
      }
    })

    
  },

  onCancel: function (e) {
    var collect_id = this.id
    var that = this

    cancelCollect({
      collect_id,
      success(data){
        that.init()
        getPrevPage()[that.callback]()
      }
    })
  }



})