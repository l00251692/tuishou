var QQMapWX = require('../../utils/qqmap-wx-jssdk.min.js');

import { host, qqmapKey } from '../../config'
var qqmapsdk;
qqmapsdk = new QQMapWX({
  key: qqmapKey
});
import { bindPhone, getImgDetailInfo } from '../../utils/api'

import { alert, datetimeFormat, getPrevPage } from '../../utils/util'

const app = getApp();
Page({
  data: {
    scale: 14,
    hiddenLoading: false
  },
  onLoad: function (options) {
    this.id = options.id
    this.img = options.img
    this.callback = options.callback || 'callback'
    this.setData({
      collect_id: this.id,
      img_url: this.img
    })
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
    var collect_id = this.id
    var img = this.img

    this.setData({
      hiddenLoading: true
    })

    getImgDetailInfo({
      collect_id,
      img_url: img,
      success(data) {
        console.log(JSON.stringify(data))
        that.setData({
          markers: [{
            iconPath: "../../images/assets/str.png",
            id: 0,
            latitude: data.latitude,
            longitude: data.longitude,
            width: 30,
            height: 30
          }, {
            iconPath: "../../images/assets/end.png",
            id: 0,
            latitude: data.latitude,
            longitude: data.longitude,
            width: 30,
            height: 30
          }],
          polyline: [{
            points: [{
              longitude: data.longitude,
              latitude: data.latitude
            }, {
              longitude: data.longitude,
              latitude: data.latitude
            }],
            color: "#FF0000DD",
            width: 4,
            dottedLine: true
          }],
          time: data.time,
          upUser_name: data.upUser_name,
          upUser_head: data.upUser_head,
          address:data.address,
          detail: data.detail,
          hiddenLoading: true
        });
      },
      error(data) {
        alert("网络错误")
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
      urls: [].join(this.data.img_url)// 需要预览的图片http链接列表
    })
  }

})