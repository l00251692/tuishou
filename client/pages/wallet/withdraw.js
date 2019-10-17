// login.js
import WxValidate from '../../utils/WxValidate'
import Countdown from '../../utils/countdown'
import { alert, confirm,  getPrevPage } from '../../utils/util'
import { applyWithDraw } from '../../utils/api'

Page({
  data: {
    codeLabel: '获取验证码',
    phone: '',
    codeNum: ''
  },
  onLoad: function (options) {
    this.balance = options.balance
    // 页面初始化 options为页面跳转所带来的参数
    this.callback = options.callback || 'callback'
    this.setData({
      balance:this.balance
    })
  },
  onReady: function () {
    // 页面渲染完成
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
  

  formSubmit(e) {
    var that = this
    var { loading } = this.data
    if (loading) {
      return;
    }

    var {  account } = e.detail.value

    if(account == null){
      alert("请输入支付宝账号")
    }

    confirm({
           content: '将提现所有余额到支付宝账号',
           cancelText: '取消',
           confirmText: '确定',
           ok() {
             console.log("account:" + account)
             applyWithDraw({
               balance: that.balance,
               account: account,
               success(data) {
                 wx.showToast({
                   title: '提现申请已提交',
                   duration: 2000,
                 })
                 getPrevPage()[that.callback]()
                 wx.navigateBack()
               }
             })
           }
    })
  }
})