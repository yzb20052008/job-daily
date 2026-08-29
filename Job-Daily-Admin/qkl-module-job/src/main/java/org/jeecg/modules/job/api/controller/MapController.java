package org.jeecg.modules.job.api.controller;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.jeecg.common.api.vo.Result;
import org.jeecg.modules.job.map.support.TencentMapApiSupport;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;

/**
 * 移动端地图服务（腾讯地图代理，密钥走 base_config）
 */
@RestController
@RequestMapping("/api/map")
@Api(tags = "移动端地图服务")
@Slf4j
public class MapController {

    @Resource
    private TencentMapApiSupport tencentMapApiSupport;

    @RequestMapping(value = "/reverseGeocoder", method = RequestMethod.GET)
    @ApiOperation(value = "逆地理编码", notes = "根据经纬度获取地址信息")
    public Result<Object> reverseGeocoder(@RequestParam(name = "latitude") String latitude,
                                          @RequestParam(name = "longitude") String longitude) {
        return tencentMapApiSupport.reverseGeocoder(latitude, longitude);
    }

    @RequestMapping(value = "/geocoder", method = RequestMethod.GET)
    @ApiOperation(value = "地理编码", notes = "根据地址获取经纬度")
    public Result<Object> geocoder(@RequestParam(name = "address") String address) {
        return tencentMapApiSupport.geocoder(address);
    }

    @RequestMapping(value = "/suggestion", method = RequestMethod.GET)
    @ApiOperation(value = "地点搜索", notes = "关键词搜索 POI")
    public Result<Object> suggestion(@RequestParam(name = "keyword") String keyword,
                                     @RequestParam(name = "latitude", required = false) String latitude,
                                     @RequestParam(name = "longitude", required = false) String longitude,
                                     @RequestParam(name = "pageSize", defaultValue = "20") Integer pageSize) {
        return tencentMapApiSupport.suggestion(keyword, latitude, longitude, pageSize);
    }

    @RequestMapping(value = "/nearby", method = RequestMethod.GET)
    @ApiOperation(value = "附近地点", notes = "地图中心点周边 POI（需登录）")
    public Result<Object> nearby(@RequestParam(name = "latitude") String latitude,
                                 @RequestParam(name = "longitude") String longitude,
                                 @RequestParam(name = "radius", defaultValue = "1000") Integer radius,
                                 @RequestParam(name = "pageSize", defaultValue = "20") Integer pageSize) {
        return tencentMapApiSupport.nearby(latitude, longitude, radius, pageSize);
    }

    @RequestMapping(value = "/ipLocation", method = RequestMethod.GET)
    @ApiOperation(value = "IP网络定位", notes = "根据客户端 IP 获取城市与坐标")
    public Result<Object> ipLocation(HttpServletRequest request,
                                     @RequestParam(name = "ip", required = false) String ip) {
        return tencentMapApiSupport.ipLocation(request, ip);
    }
}
