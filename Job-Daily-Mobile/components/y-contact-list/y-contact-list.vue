<template>
	<view class="y-contact-list">
		<view class="contact-item" v-for="(item,index) in list" :key="index" @click="toPost(item)">
			<view class="item-top">
				<view class="top-left">
					<text class="name">{{item.userName}}</text>
					<text class="phone" v-if="item.contact">{{phoneHiden(item.contact)}}</text>
					<text class="phone" v-else>{{phoneHiden(item.phone)}}</text>
					<view class="tags">
						<view class="tags-item">
							<u-tag v-if="userInfo.memberRole=='member'" text="老板" size="mini" color="#007aff" borderColor="#E5F4FF" bgColor="#E5F4FF"></u-tag>
							<u-tag v-else text="工人" size="mini" color="#007aff" borderColor="#E5F4FF" bgColor="#E5F4FF"></u-tag>
						</view>
					</view>
				</view>
				<view class="top-right" v-if="item.roleCode!=userInfo.memberRole">
					<text class="right-icon yzb yzb-24gf-phoneIncoming"></text>
					<text class="right-title">呼入</text>
				</view>
				<view class="top-right" v-else>
					<text class="right-icon2 yzb yzb-24gf-phoneOutgoing"></text>
					<text class="right-title2">呼出</text>
				</view>
			</view>
			<text class="desc" v-if="item.title">{{item.title}}</text>
			<text class="time">{{item.createTime}}</text>
			<view class="info-btn">
				<view @click.stop="toDelete(item)">
					<u-button :customStyle="{width:'70px',marginLeft:'10px',height:'30px',color:'#333'}" plain type="info"
						shape="circle">删除</u-button>
				</view>
				<view @click.stop="phoneCall(item)">
					<u-button :customStyle="{width:'70px',marginLeft:'10px',height:'30px',color:'#333'}" plain type="info"
						shape="circle">联系</u-button>
				</view>
			</view>
		</view>
	</view>
</template>

<script>
	import {
		mapState
	} from 'vuex';
	import {
		phoneHiden
	} from '@/config/common';
	export default {
		name: 'y-contact-list',
		computed: {
			...mapState(['userInfo'])
		},
		options: {
			styleIsolation: 'shared'
		},
		props: {
			list: {
				type: Array
			},
			url: {
				type: String
			}
		},

		data() {
			return {};
		},

		filters: {
		},

		methods: {
			
			phoneHiden(val) {
				return phoneHiden(val);
			},
			
			// 电话客服
			phoneCall(item) {
				let that =this ;
				let phone=item.phone;
				if(item.contact){
					phone=item.contact;
				}
				console.log("phone==", phone)
				uni.makePhoneCall({
					phoneNumber: phone,
					success: () => {
						console.log('成功拨打电话');
					},
					fail: (err) => {
						console.log(err);
					},
				});
			},
			
			toPost(item){
				if(item.postId){
					uni.$u.route("/pages/job/postDetail?id=" + item.postId);
				}
			},
			
			toDelete(item){
				let that=this;
				uni.showModal({
					title: "温馨提示",
					content: "确定移除该记录？",
					confirmText: "确定",
					cancelText: "取消",
					success: (data) => {
						if (data.confirm) {
							that.doDelete(item);
						}
					}
				});
				
			},
			
			async doDelete(item){
				let params={
					id:item.id
				}
				let res = await this.$apis.delContact(params);
				console.log("addCallRecord",res);
				if (res) {
					this.$emit('success', item)
					uni.$u.toast('删除成功');
				}
			},
		}
	};
</script>

<style lang="scss" scoped>
	
	page {
		background-color: #f5f6fa;
	}
	
	.y-contact-list {}

	.contact-item {
		display: flex;
		flex-direction: column;
		margin-bottom: 20upx;
		box-sizing: border-box;
		background-color: #FFFFFF;
		border-radius: 20upx;
		padding: 20upx;
	}
	
	.item-top{
		display: flex;
		flex-direction: row;
		justify-content: space-between;
		align-items: center;
		
		.top-left{
			display: flex;
			flex-direction: row;
			align-items: center;
			justify-content: space-between;
			
			.name{
				font-weight: bold;
				font-size: 18px;
			}
			.phone{
				margin: 0 15upx;
				font-size: 15px;
				color: #333;
			}
			
			.tags{
				display: flex;
				flex-direction: row;
				flex-wrap: wrap;
				
				.tags-item {
					margin-right: 15upx;
				}
			}
		}
		
		.top-right{
			
			.right-icon{
				font-size: 14px;
				color: #f0ad4e;
			}
			
			.right-title{
				font-size: 14px;
				color: #f0ad4e;
				margin-left: 5upx;
			}
			
			.right-icon2{
				font-size: 14px;
				color: #007aff;
			}
			
			.right-title2{
				font-size: 14px;
				color: #007aff;
				margin-left: 5upx;
			}
		}
	}
	
	.desc{
		font-size: 15px;
		color: #333;
		padding: 10upx 0;
	}
	
	.time{
		font-size: 13px;
		color: #888;
	}

	.info-btn {
		display: flex;
		flex-direction: row;
		justify-content: flex-end;
		align-items: center;
		border-top: 1upx solid #eee;
		padding: 20upx 20upx;
		margin-top: 20upx;
	}

</style>