import {
  ORDER_STATES
} from './../task/constant'
import {
  getCollects,
  getBalanceToWx,
  getWithDraw
} from '../../utils/api'

import {
  datetimeFormat,
  alert,
  confirm
} from '../../utils/util'


Page({
  data: {

    page: 0,
    withdraw: null,
    ORDER_STATES
  },
  onLoad: function(optisons) {
    this.balance = optisons.balance
    this.callback = optisons.callback | 'callback'
    this.initData()
  },
  onShow: function() {
    // 页面显示
  },
  onHide: function() {
    // 页面隐藏
  },
  onUnload: function() {
    // 页面关闭
  },

  initData(cb) {
    this.setData({
      page: 0,
      hasMore: true,
      loading: false,
      list: null
    })
    this.loadData(cb)
  },

  loadData(cb) {
    var that = this
    var { loading, page } = this.data

    if (loading) {
      return
    }

    this.setData({
      loading: true,
      balance: this.balance
    })

    getCollects({
      page,
      success(data) {
        var list_old = that.data.list
        var {
          list,
          count
        } = data
        console.log("myorderlist:" + JSON.stringify(data))
        that.setData({
          loading: false,
          list: list_old ? list_old.concat(list) : list,
          hasMore: count == 5,
          page: page + 1
        })
        console.log("get list  ok")
        cb && cb()

      },
      error(data) {
        that.setData({
          loading: false,
          hasMore: false
        })
      }
    })
  },

  onReachBottom(e) {
    this.loadData()
  },

  onPullDownRefresh() {
    console.log("onPullDownRefresh")
    wx.showNavigationBarLoading()
    this.loadData(() => {
      wx.hideNavigationBarLoading()
      wx.stopPullDownRefresh()
    })
  },

  onBalanceBack() {
    var balance = this.balance
    if (parseFloat(balance) > 0.0) {
      wx.navigateTo({
        url: '/pages/wallet/withdraw?callback=callback&&balance=' + balance,
      })
    } else {
      alert("余额大于0才可以提现")
    }

    //   var balance = this.ballance

    //   console.log("tixianjie:" + balance)

    //   if (parseFloat(balance) > 0.0){
    //     confirm({
    //       content: '将提现所有余额到微信零钱',
    //       cancelText: '取消',
    //       confirmText: '确定',
    //       ok() {
    //         getBalanceToWx({
    //           money: balance,
    //           success(data){
    //             wx.showToast({
    //               title: '提现申请成功',
    //             })
    //           },
    //           error(data){}
    //         })
    //         wx.showToast({
    //           title: '提现申请失败，请联系客服',
    //         })
    //       }
    //     })
    //   }
    //   else{
    //     alert("余额大于0才可以提现")
    //   }
  },

  callback() {
    this.initData()
  }

});