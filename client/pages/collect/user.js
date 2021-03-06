import {
  ORDER_STATES
} from './../task/constant'
import {
  selectCollectsByuserId
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
  onLoad: function (optisons) {
    this.project_id = optisons.id
    this.user_id = optisons.user_id
    this.initData()
    this.loadData()
  },
  onShow: function () {
    // 页面显示
  },
  onHide: function () {
    // 页面隐藏
  },
  onUnload: function () {
    // 页面关闭
  },

  initData() {
    this.setData({
      page: 0,
      hasMore: true,
      loading: false,
      list: null
    }) 
  },



  loadData(cb) {
    var that = this
    var {  loading,  page } = this.data

    if (loading) {
      return
    }

    this.setData({
      loading: true
    })

    selectCollectsByuserId({
      project_id: that.project_id,
      user_id: that.user_id,
      page,
      success(data) {
        var list_old = that.data.list
        var { list,count } = data

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
          loading: false
        })
      }
    })
  },

  onReachBottom(e) {
    this.loadData()
  },

  onPullDownRefresh() {
    wx.showNavigationBarLoading()
    this.initData()
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
    this.loadData()
  }
});