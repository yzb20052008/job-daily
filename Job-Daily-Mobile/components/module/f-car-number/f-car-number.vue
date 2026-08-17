<template>
    <view>
        <!-- 基于uview2.0 u-keyboard封装车牌号输入组件 -->
        <view class="carIdBox u-flex">
            <block v-for="(item,index) in carId" :key="index" v-if="index<2">
                <view class="box" :class="{'box-border':current===index,'box-bg':index===0}" @click="openKeyboard(index)">{{item}}</view>
            </block>
            <view class="dian">·</view>
            <block v-for="(item,index) in carId" :key="index" v-if="index>=2 && index<=7">
                <view class="box" :class="{'new':index==7,'box-border':current===index}" @click="openKeyboard(index)">
                    <block v-if="!(index==7 && item=='')">{{item}}</block>
                    <view class="newText" v-else>新能源</view>
                </view>
            </block>
        </view>
        <u-keyboard ref="uKeyboard" mode="car" :show="carShow" :showCancel="false" :overlayOpacity="0" @change="keyboardChange" @confirm="closeKeyboard" @close="closeKeyboard" @backspace="backspace"></u-keyboard>
    </view>
</template>

<script>
export default {
    data() {
        return {
            carId:['浙','A','','','','','',''],//车牌号
            current:-1,
            carShow:false,
        };
    },
    //方法
    methods: {
        // 打开键盘
        openKeyboard(index){
            if(this.carId[index]==''){
                if(index===0){
                    this.current = index
                }else if(index===1){
                    this.current = index
                }else{
                    for(var i=0;i<this.carId.length;i++){
                        if(this.carId[i]==''){
                            this.current = i
                            break;
                        }
                    }
                }
                console.log(this.current,'this.current')
                this.carShow = true
            }else{
                this.current = index
                this.carShow = true
            }
        },
        keyboardChange(e){
            console.log(e,'e')
            this.carId[this.current] = e
            var isOk = true
            for(var i=0;i<this.carId.length;i++){
                if(this.carId[i]==''){
                    isOk = false
                    this.current = i
                    break;
                }
            }
            if(isOk){
                this.closeKeyboard()
            }
            this.$emit('keyboardChange',this.carId)
        },
        // 关闭键盘
        closeKeyboard(){
            this.current = -1
            this.carShow = false
        },
        // 退格键被点击
        backspace(){
            this.$set(this.carId,this.current,'')
            if(this.current<=0){
                this.current = 0
            }else{
                this.current--
            }
            this.$emit('backspace',this.carId)
        }

    }
};
</script>
<style lang="scss" scoped>
.carIdBox{
    justify-content: space-between;
    .box{
        width: 70rpx;
        height: 80rpx;
        display: flex;
        align-items: center;
        justify-content: center;
        border-radius: 8rpx;
        background-color: #eee;
        text-align: center;
        font-size: 40rpx;
        &-bg{
            background-color: #E1EFFF;
        }
        &-border{
            border: 4rpx solid #D9E4F0;
        }
        .newText{
            font-size: 20rpx;
            color: #B3D0AB;
        }
    }
    .new{
        background-color: #F6FAF5;
        border: 1rpx solid #DFEBDC;
    }
    .dian{
        color: #eee;
        font-size: 40rpx;
        font-weight: bold;
    }
}
</style>
