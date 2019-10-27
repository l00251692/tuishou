import {
  ORDER_STATES
} from './../task/constant'
import {
  getTaskCollects,
  getTaskCollectSummary,
  getBalanceToWx
} from '../../utils/api'

import {
  datetimeFormat,
  alert,
  confirm
} from '../../utils/util'

Page({
  data: {
    ORDER_STATES,
    page: 0,
    hasMore: true,
    loading: false,
    list: null,
    all: '-',
    pass: '-',
    toCheck: '-',
    reject: '-',
    type: 0,
  },
  onLoad: function(optisons) {
    this.id = optisons.id
    this.initData()
    this.loadData()
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
  },

  loadData(cb) {
    var that = this
    var project_id = this.id
    console.log("task collect loadData:" + project_id)
    

    getTaskCollectSummary({
      project_id,
      success(data){
        that.setData({
          all: data.all,
          pass: data.pass,
          toCheck: data.toCheck,
          reject: data.reject,
        })

      },
      error(res){
        that.setData({
          all: '-',
          pass: '-',
          toCheck:'-',
          reject: '-',
        })
      }
    })

    this.getTaskCollectsByType()

    
  },

  getTaskCollectsByType(){
    var that = this
    var project_id = this.id
    var { loading, type, page } = this.data

    if (loading) {
      return
    }

    this.setData({
      loading: true,
    })

    getTaskCollects({
      page,
      type,
      project_id,
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
      },
      error(data) {
        that.setData({
          loading: false
        })
      }
    })
  },

  onReachBottom(e) {
    this.getTaskCollectsByType()
  },

  onPullDownRefresh() {
    this.initData()
    this.loadData()
  },

  callback() {
    console.log("call collects callback")
    this.initData()
    this.setData({
      type:0
    })
    this.loadData()
  },

  getAll(e){
    console.log("getAll")
    this.initData()
    this.setData({
      type:0
    })
    this.getTaskCollectsByType()
  },

  getPass(e) {
    this.initData()
    this.setData({
      type: 2
    })
    this.getTaskCollectsByType()
  },

  getToCheck(e) {
    this.initData()
    this.setData({
      type: 1
    })
    this.getTaskCollectsByType()
  },

  getReject(e) {
    this.initData()
    this.setData({
      type: 3
    })
    this.getTaskCollectsByType()
  },

  onBalanceBack() {

    confirm({
      content: '受微信限制暂不支持提现到零钱，请联系客服留言提现',
      cancelText: '取消',
      confirmText: '确定',
      ok() {
        wx.showToast({
          title: '谢谢您的理解',
        })
      }
    })

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
  }

});