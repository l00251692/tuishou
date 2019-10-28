import {
  getRegisterInfo, updateLocation, bindPhone
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
    nickNmae:'',
    disabled:true
  },
  onLoad: function(options) {
    
    if (options.scene) {
      console.log("has scene");
      var scene = decodeURIComponent(options.scene);
      console.log("scene is ", scene);
      var arrPara = scene.split("&");
      var arr = [];
      for (var i in arrPara) {
        arr = arrPara[i].split("=");
        console.log("log:", arr[0], "=", arr[1]);
        if (arr[0] == 'id') {
          this.setData({
            project_id: arr[1]
          })
        }
        if (arr[0] == 'from_user_id')
        {
          this.setData({
            from_user_id: arr[1]
          })
        }
      }
    } else {
      console.log("no scene");
      this.setData({
        project_id: ''
      })
    }

  },
  onReady: function() {

  },

  onLogin(e) {
    console.log("to login")
    var that = this
    console.log(e.detail.errMsg)
    if (e.detail.errMsg == 'getUserInfo:ok') {

      getApp().getLoginInfo(loginInfo => {
        that.setData({
          nickName: loginInfo.userInfo.nickName
        })
      })
    }
  },

  bindPhoneNumber: function(e) {
    console.log("bind phone" + JSON.stringify(e))
    if (e.detail.errMsg !== "getPhoneNumber:ok") {
      // 拒绝授权
      return;
    }

    var that = this
    var nickName = this.data.nickName
    if (nickName == '') {
      alert("请先授权获取昵称信息")
    }

    var addr_string = this.data.addr_string

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
            title: '获取手机号成功',
          })
          that.setData({
            phone: data.phone
          })

          if (addr_string != "")
          {
            that.setData({
              disabled: false
            })
          }
        },
        error(res) {
          alert("获取手机号失败")
        }
      })
    })

  },

  onChooseLocation: function() {
    console.log("onChooseLocation")
    var that = this

    var nickName = this.data.nickName
    if (nickName == '') {
      alert("请先授权获取昵称信息")
    }

    var phone = this.data.phone

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

                if (phone != "") {
                  that.setData({
                    disabled: false
                  })
                }
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
    var { nickName, phone, addr_string, location} = this.data

    if (nickName == '') {
      return alert("请先授权获得昵称信息")
    }

    if(phone == '')
    {
      return alert("请先授权绑定手机")
    }

    if (addr_string == '')
    {
      return alert("请选择区域信息")
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
        var userInfo = wx.getStorageSync("userInfo")
        console.log("userinf1:" +JSON.stringify(userInfo))
        userInfo.phone = phone
        userInfo.city = location.city
        userInfo.province = location.province
        console.log("userinf2:" + JSON.stringify(userInfo))
        wx.setStorageSync("userInfo", userInfo)
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