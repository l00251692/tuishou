var QQMapWX = require('../../utils/qqmap-wx-jssdk.min.js');

import { host, qqmapKey } from '../../config'
var qqmapsdk;
qqmapsdk = new QQMapWX({
  key: qqmapKey
});
import { bindPhone } from '../../utils/api'

import { alert, datetimeFormat, getPrevPage } from '../../utils/util'

const app = getApp();
Page({
  data: {
    scale: 14,
    hiddenLoading: false
  },
  onLoad: function (options) {
    this.id = options.id
    this.callback = options.callback || 'callback'
    this.loadData()
  },

  onShow() {
    this.mapCtx = wx.createMapContext("didiMap");
    this.movetoPosition();
  },

  onReady() {

  },

  movetoPosition: function () {
    this.mapCtx.moveToLocation();
  },

  loadData() {
    var that = this
    var order_id = this.id

    this.setData({
      hiddenLoading: true
    })

    return

    getOrderInfo({
      order_id,
      success(data) {

        var order = data.order

        order.createTime = datetimeFormat(order.createTime)
        that.setData({
          markers: [{
            iconPath: "../../images/assets/str.png",
            id: 0,
            latitude: data.order.strLatitude,
            longitude: data.order.strLongitude,
            width: 30,
            height: 30
          }, {
            iconPath: "../../images/assets/end.png",
            id: 0,
            latitude: data.order.endLatitude,
            longitude: data.order.endLongitude,
            width: 30,
            height: 30
          }],
          polyline: [{
            points: [{
              longitude: data.order.strLongitude,
              latitude: data.order.strLatitude
            }, {
              longitude: data.order.endLongitude,
              latitude: data.order.endLatitude
            }],
            color: "#FF0000DD",
            width: 4,
            dottedLine: true
          }],
          passenger: data.passenger,
          order: order,
          hiddenLoading: true
        });
      },
      error(data) {
        alert("查看详细信息失败，请联系客服")
      }
    })
  },

  onScaleSub(e) {
    if (this.data.scale > 0) {
      this.setData({
        scale: --this.data.scale
      })
    }

  },

  onScalePlus(e) {
    if (this.data.scale < 18) {
      this.setData({
        scale: ++this.data.scale
      })
    }
  },

  onRevert(e) {
    this.mapCtx.moveToLocation();
  },

  bindregionchange: (e) => {

  },
  //点击merkers
  bindmarkertap(e) {
    // console.log(e.markerId)
    // wx.showActionSheet({
    //   itemList: ["A"],
    //   success: function (res) {
    //     console.log(res.tapIndex)
    //   },
    //   fail: function (res) {
    //     console.log(res.errMsg)
    //   }
    // })
  },

  
  toCheckImg() {
    wx.previewImage({
      urls: ["https://wx.qlogo.cn/mmhead/Yr1LMYX6KTaOlic5qThibhniaX1T6hiciaLYsFhy1tIC8UQw/132" ]// 需要预览的图片http链接列表
    })
  }

})