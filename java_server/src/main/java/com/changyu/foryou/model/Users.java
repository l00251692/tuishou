package com.changyu.foryou.model;

import java.util.Date;

public class Users {
	
	private String userId;
	
    private String phone;

    private Short  type;

    private String nickname;

    private String imgUrl;

    private Date lastLoginDate;
          
    private Short sex;
    
    private String latitude;
    
    private String longitude;
    
    private String province;
    
    private String city;
    
    private String district;
    
    private String address;
    
    private float balance;
    
    private String fromUserId;
    
    private String fromProjectId;
    
    private String fromRecords;
    
    
	public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone == null ? null : phone.trim();
    }


    public Short getType() {
        return type;
    }

    public void setType(Short type) {
        this.type = type;
    }

    public String getNickname() {
        return nickname;
    }

    public void setNickname(String nickname) {
        this.nickname = nickname == null ? null : nickname.trim();
    }

    public String getImgUrl() {
        return imgUrl;
    }

    public void setImgUrl(String imgUrl) {
        this.imgUrl = imgUrl == null ? null : imgUrl.trim();
    }

    public Date getLastLoginDate() {
        return lastLoginDate;
    }

    public void setLastLoginDate(Date lastLoginDate) {
        this.lastLoginDate = lastLoginDate;
    }



	public Short getSex() {
		return sex;
	}

	public void setSex(Short sex) {
		this.sex = sex;
	}


	public String getUserId() {
		return userId;
	}


	public void setUserId(String userId) {
		this.userId = userId;
	}

	public String getLatitude() {
		return latitude;
	}

	public void setLatitude(String latitude) {
		this.latitude = latitude;
	}

	public String getLongitude() {
		return longitude;
	}

	public void setLongitude(String longtitude) {
		this.longitude = longtitude;
	}

	public float getBalance() {
		return balance;
	}

	public void setBalance(float balance) {
		this.balance = balance;
	}

	public String getProvince() {
		return province;
	}

	public void setProvince(String province) {
		this.province = province;
	}

	public String getCity() {
		return city;
	}

	public void setCity(String city) {
		this.city = city;
	}

	public String getDistrict() {
		return district;
	}

	public void setDistrict(String distinct) {
		this.district = distinct;
	}

	public String getAddress() {
		return address;
	}

	public void setAddress(String address) {
		this.address = address;
	}

	public String getFromUserId() {
		return fromUserId;
	}

	public void setFromUserId(String fromUserId) {
		this.fromUserId = fromUserId;
	}

	public String getFromProjectId() {
		return fromProjectId;
	}

	public void setFromProjectId(String fromProjectId) {
		this.fromProjectId = fromProjectId;
	}

	public String getFromRecords() {
		return fromRecords;
	}

	public void setFromRecords(String fromRecords) {
		this.fromRecords = fromRecords;
	}
}