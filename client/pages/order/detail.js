// pages/order/detail.js

import {
  getOrderInfo, getPayment, updateOrderPayed
} from '../../utils/api'

import {
  alert, requestPayment, getPrevPage
} from '../../utils/util'

Page({

  /**
   * 页面的初始数据
   */
  data: {
    // state: [{ status: '已完成', name: '受理', type: 'finished', list: { '受理人': '六六六', "受理部门": "哈哈哈部门" } }, { status: '已完成', name: '审核', type: 'finished', list: { "审核人": "七七七" } }, { status: '已完成', name: '申请', type: 'finished', list: { '申请人': '六六六', "审核人": "七七七" } }],
   canClick:true

  
  },

  /**
   * 生命周期函数--监听页面加载
   */
  onLoad: function (options) {
    this.id = options.id
    this.callback = options.callback
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
    var order_id = this.id

    getOrderInfo({
      order_id,
      success(data){
        that.setData({
          info:data.info,
          state: data.state
        })
      },
      error(){
        console.log("获取订单信息失败")
      }
    })
  },

  payTap(e){
    var order_id = this.data.info.order_id
    var pay_money = "200.00"
    var that = this
    this.setData({
      canClick: false
    })

    getPayment({
      order_id,
      pay_money,
      success(data) {
        //发起微信支付
        console.log("getPayment success:")
        requestPayment({
          data,
          success() {
            //更新订单状态
            updateOrderPayed({
              order_id,
              success(data) {
                getPrevPage()[that.callback]()
                that.init()
                that.setData({
                  canClick: true
                })
              },
              error(data){
                that.setData({
                  canClick: true
                })
              }
            })
          },
          error(data) {
            console.log("用户取消支付")
            that.setData({
              canClick: true
            })
          }
        })

      }, error(data) {
        console.log("getPayment err:" + JSON.stringify(data))
        that.setData({
          canClick: true
        })
      }
    })
  },

  previewImage: function (e) {
    var { front_img, back_img } = this.data.info
    var imgalist = [front_img, back_img]

    var current = e.target.dataset.src;
    wx.previewImage({
      current: current, // 当前显示图片的http链接
      urls: imgalist // 需要预览的图片http链接列表
    })
  },

  previewImage2: function (e) {
    var { summary_img } = this.data.info
    var imgalist = [summary_img]

    var current = e.target.dataset.src;
    wx.previewImage({
      current: current, // 当前显示图片的http链接
      urls: imgalist // 需要预览的图片http链接列表
    })
  },

  updateIdCard(e){
    var order_id = this.data.info.order_id

    wx.navigateTo({
      url: '/pages/upload/updateIdCard?id=' + order_id,
    })
  },

  repayService(e){
    var order_id = this.data.info.order_id
    var pay_money = "50.00"
    var that = this
    this.setData({
      canClick: false
    })
    //对于重新支付的订单需要与之前的订单号不一样，微信对于同一订单不能多次支付
    var order_id2 = order_id + 'RE'
    getPayment({
      order_id: order_id2,
      pay_money,
      success(data) {
        //发起微信支付
        requestPayment({
          data,
          success() {
            //更新订单状态
            updateOrderPayed({
              order_id,
              success(data) {
                getPrevPage()[that.callback]()
                that.init()
                that.setData({
                  canClick: true
                })
              },
              error(data){
                that.setData({
                  canClick: true
                })
              }
            })
          },
          error(data) {
            console.log("支付失败")
            that.setData({
              canClick: true
            })
          }
        })

      }, error(data) {
        console.log("getPayment err:" + JSON.stringify(data))
        that.setData({
          canClick: true
        })
      }
    })
  }


})