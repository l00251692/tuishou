const qiniuUploader = require("../../utils/qiniuUploader")
import {
  alert, randomString
} from '../../utils/util'

import {
  getOrderInfo, updateOrderIdCard, getQiniuToken
} from '../../utils/api'

Page({

  /**
   * 页面的初始数据
   */
  data: {
    idCardFrontFlag: false,
    idCardBackFlag: false
  },

  /**
   * 生命周期函数--监听页面加载
   */
  onLoad: function (options) {
    this.id = options.id

    this.setData({
      order_id : this.id
    })

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

  init() {
    var that = this
    var order_id = this.id

    getOrderInfo({
      order_id,
      success(data) {
        that.setData({
          idCardFrontPath: data.info.front_img,
          idCardBackPath: data.info.back_img
        })
        wx.removeStorageSync('idCardFrontPath')
        wx.removeStorageSync('idCardBackPath')
      },
      error() {
        console.log("获取订单信息失败")
      }
    })
  },

  onChooseFront(e) {
    wx.navigateTo({
      url: '/pages/upload/takePhoto?type=1&callback=callback',
    })
  },

  onChooseBack(e) {
    wx.navigateTo({
      url: '/pages/upload/takePhoto?type=2&callback=callback',
    })
  },

  updateIdCard(e) {

    var { idCardFrontPath, idCardBackPath, idCardFrontFlag, idCardBackFlag } = this.data

    if (idCardFrontFlag != true){
      alert("身份证正面照片未更新请重新上传")
      return
    }

    if (idCardBackFlag != true) {
      alert("身份证背面照片未更新请重新上传")
      return
    }

    var token = ''
    var order_id =this.id
    var key_str = randomString(4) //随机4位字符串用于保密
    getQiniuToken({
      success(data) {
        token = data.upToken;
        qiniuUploader.upload(idCardFrontPath, (res) => {
          var front_img = res.imageURL
          //上传反面身份证照片
          qiniuUploader.upload(idCardBackPath, (res) => {
            var back_img = res.imageURL
            //更新照片信息到订单信息中
            updateOrderIdCard({
              front_img,
              back_img,
              order_id,
              success(data) {
                //获取支付参数
                alert("照片上传成功",function(){
                    wx.switchTab({
                      url: '/pages/mine/mine',
                    })
                })    
              },
              error(data) {
                alert("照片上传失败，请稍候再试")
              }

            })

          }, (error) => {
            alert("上传身份证照片失败")
          }, {
              region: 'ECN', //华东
              domain: 'img.ailogic.xin',
              key: 'order_' + order_id + '_' + key_str + '_back',
              uptoken: token
            }, (res) => {
            });

        }, (error) => {
          console.log('error: ' + error);
          alert("上传身份证照片失败")
        }, {
            region: 'ECN', //华东
            domain: 'img.ailogic.xin',
            key: 'order_' + order_id + '_' + key_str + '_front',
            uptoken: token
          }, (res) => {

          });
      },
      error(data) {
        alert("上传身份证照片失败，请稍候")
      }
    })

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

  },

  /**
   * 用户点击右上角分享
   */
  onShareAppMessage: function () {

  },

  callback(e) {
    var that = this
    var tmp1 = wx.getStorageSync('idCardFrontPath')
    if (tmp1) {
      wx.getFileInfo({
        filePath: tmp1,
        success(res) {
          that.setData({
            idCardFrontPath: tmp1,
            idCardFrontFlag:true
          })
        },
        fail(res) {
          that.setData({
            idCardFrontPath: null
          })
        }
      })

    }

    var tmp2 = wx.getStorageSync('idCardBackPath')
    if (tmp2) {
      wx.getFileInfo({
        filePath: tmp2,
        success(res) {
          that.setData({
            idCardBackPath: tmp2,
            idCardBackFlag: true
          })
        },
        fail(res) {
          that.setData({
            idCardBackPath: null
          })
        }
      })
    }
  }
})