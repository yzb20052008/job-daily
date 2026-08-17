<template>
    <!-- 签名横屏 -->
	<view class="signa">
        <view class="box">
            <canvas class="canvas" disable-scroll="true" :style="{'width':width,'height':height}" canvas-id="designature" @touchstart="starts"
             @touchmove="moves" @touchend="end"></canvas>
            <view class="bottomBox u-flex">
                <view class="tip">请你在此手动签写自己的签章，签写操作完毕点击保存按钮</view>
                <view class="btn" style="margin-top: 18vw;" @click="cancel">取消</view>
                <view class="btn" style="margin-top: 8vw;" @click="clear">清除</view>
                <view class="btn blue" @click="save">保存</view>
            </view>
        </view>
		
	</view>
</template>

<script>
export default {
    data() {
        return {
            resultUrl: '',
            dom: null,
            line: [],
            width: '0px',
            height: '0px',
            radius: 0
        }
    },
    created() {
        uni.getSystemInfo({
            success: (res) => {
                this.width = res.windowWidth - 56 + 'px';
                this.height = res.windowHeight - 154 + 'px';
            }
        });
        this.dom = uni.createCanvasContext('designature',this);
    },
    methods: {
        cancel(){
            uni.navigateBack()
        },
        end(e) {

        },
        distance(a, b) {
            let x = b.x - a.x;
            let y = b.y - a.y;
            return Math.sqrt(x * x + y * y);
        },
        starts(e) {
            console.log(e)
            this.line.push({
                points: [{
                    time: new Date().getTime(),
                    x: e.touches[0].x,
                    y: e.touches[0].y,
                    dis: 0
                }]
            })
            let currentPoint = {
                x: e.touches[0].x,
                y: e.touches[0].y
            }
            this.currentPoint = currentPoint
            this.drawer(this.line[this.line.length - 1])
        },
        moves(e) {
            console.log(e)
            let point = {
                x: e.touches[0].x,
                y: e.touches[0].y
            }
            this.lastPoint = this.currentPoint,
                this.currentPoint = point
            this.line[this.line.length - 1].points.push({
                time: new Date().getTime(),
                x: e.touches[0].x,
                y: e.touches[0].y,
                dis: this.distance(this.currentPoint, this.lastPoint)
            })
            this.drawer(this.line[this.line.length - 1])
        },
        drawer(item) {
            let x1, x2, y1, y2, len, radius, r, cx, cy, t = 0.5,
                x, y;
            var time = 0;
            if (item.points.length > 2) {
                let lines = item.points[item.points.length - 3];
                let line = item.points[item.points.length - 2];
                let end = item.points[item.points.length - 1];
                x = line.x;
                y = line.y;
                x1 = lines.x;
                y1 = lines.y;
                x2 = end.x;
                y2 = end.y;
                var dis = 0;
                time = (line.time - lines.time) + (end.time - line.time)
                dis = line.dis + lines.dis + end.dis;
                var dom = this.dom;
                var or = Math.min(time / dis * this.linePressure + this.lineMin, this.lineMax);
                cx = (x - (Math.pow(1 - t, 2) * x1) - Math.pow(t, 2) * x2) / (2 * t * (1 - t))
                cy = (y - (Math.pow(1 - t, 2) * y1) - Math.pow(t, 2) * y2) / (2 * t * (1 - t))
                dom.setLineCap('round')
                dom.beginPath();
                dom.setStrokeStyle('black')
                dom.setLineWidth(5)
                dom.moveTo(x1, y1);
                dom.quadraticCurveTo(cx, cy, x2, y2);
                
                dom.stroke();
                dom.draw(true)
            }

        },
        clear() {
            this.dom.clearRect(0, 0, 1000, 1000)
            this.dom.draw()
        },
        save() {
            var t=this;
            console.log(555)
            uni.canvasToTempFilePath({
                canvasId: 'designature',
                fileType: 'png',
                quality: 1, //图片质量
                success:function(res) {
                    console.log(res.tempFilePath)
                    t.$emit('getImg',res.tempFilePath)
                },
                fail(e){
                    console.log(e)
                }
            },this)
        }
    }
}
</script>

<style scoped lang="scss">
.signa {
    position: relative;
    overflow: hidden;
    background-color: #fff;
    height: 100vh;
    width: 100vw;
    padding: 20px 16px 30px;
    .box{
        background-color: #fff;
        border-radius: 20rpx;
        padding: 12px 12px 100px;
        box-shadow: 0 6rpx 42rpx rgba(0, 0, 0, 0.05);
    }
    .canvas {
        background-color: #E6E6E6;
        position: relative;
        z-index: 9999;
    }
    .bottomBox{
        flex-direction: column;
        justify-content: center;
        width: 100px;
        height: 80vw;
        padding: 10px;
        // background-color: #007AFF;
        position: fixed;
        left: 50%;
        bottom: -40vw;
        transform: rotate(90deg);
        margin-left: -50px;
        margin-bottom: 72px;
        .tip{
            font-size: 11px;
            color: #FF6F77;
        }
        .btn{
            width: 100%;
            height: 37px;
            background: #F5F5F5;
            border-radius: 19px;
            margin-top: 7px;
            text-align: center;
            line-height: 37px;
            color: #333333;
            font-size: 13px;
            &.blue{
                background: #284CF4;
                color: #fff;
            }
        }
    }
}
</style>
