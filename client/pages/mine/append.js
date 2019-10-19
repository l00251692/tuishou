import {
  getRegisterInfo, updateLocation
} from '../../utils/api'

import {
  alert,
  reverseGeocoder,
  getAddressFromLocation
} from '../../utils/util'
var app = getApp();
Page({
  data: {
    loading: false,
    phone:'',
    addr_string:'',

  },
  onLoad: function() {
    this.getRegisterInfo()
  },
  onReady: function() {

  },

  getRegisterInfo(){
    var that = this
    getRegisterInfo({
      success(data){
        that.setData({
          phone: data.phone,
          addr_string:data.addr
        })
      },
      error(res){
        console.log("get registerinfo fail")
      }
    })
  },

  bindPhoneNumber: function(e) {
    console.log("bind phone" + JSON.stringify(e))
    if (e.detail.errMsg !== "getPhoneNumber:ok") {
      // 拒绝授权
      return;
    }

    var that = this
    var ency = e.detail.encryptedData;
    var iv = e.detail.iv;
    getApp().getLoginInfo(loginInfo => {
      var {
        session_key
      } = wx.getStorageSync('userInfo');

      bindPhone({
        ency,
        iv,
        session_key,
        success(data) {
          wx.showToast({
            title: '绑定手机号成功',
          })
          that.setData({
            phone: data.phone
          })
        },
        error(res) {
          alert("绑定手机号失败")
        }
      })
    })

  },

  onChooseLocation: function() {
    console.log("onChooseLocation")
    var that = this

    wx.authorize({
      scope: 'scope.userLocation',
      success() {
        console.log("wx authorize success")
        wx.chooseLocation({
          success: function(res) {
            var {
              name,
              address,
              longitude,
              latitude
            } = res
            var location = {
              longitude,
              latitude
            }

            reverseGeocoder({
              location,
              success(data) {
                console.log(data)
                that.setData({
                  location: Object.assign({
                    name,
                    address,
                    location
                  }, data),
                  addr_string: data.city + data.district,
                })
                console.log(JSON.stringify(that.data.location))
              }
            })
          },
        })
      },
      fail(res) {
        alert("未授权获位置信息将影响相关功能，请点击右上角设置按钮进行授权")
      }
    })
  },

  confirm: function() {
    var that = this;
    var { phone, addr_string, location} = this.data

    if(phone == '')
    {
      alert("请先授权绑定手机")
    }

    if (addr_string == '')
    {
      alert("请选择区域信息")
    }

    updateLocation({
      longitude: location.location.longitude,
      latitude: location.location.latitude,
      province: location.province,
      city: location.city,
      district: location.district,
      name:location.name,
      success(data) {
        console.log("update location success")
        wx.showToast({
          title: '保存成功',
        })
      },
      error(res) {
        console.log("update location fail")
        wx.showToast({
          title: '保存失败',
        })
      }
    })
  }
});