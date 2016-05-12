package com.example.keng.smark_stick;

import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ToggleButton;
import com.baidu.location.BDLocation;
import com.baidu.location.BDLocationListener;
import com.baidu.location.LocationClient;
import com.baidu.location.LocationClientOption;
import com.baidu.mapapi.SDKInitializer;
import com.baidu.mapapi.map.BaiduMap;
import com.baidu.mapapi.map.BitmapDescriptor;
import com.baidu.mapapi.map.BitmapDescriptorFactory;
import com.baidu.mapapi.map.InfoWindow;
import com.baidu.mapapi.map.MapView;
import com.baidu.mapapi.map.Marker;
import com.baidu.mapapi.map.MarkerOptions;
import com.baidu.mapapi.map.OverlayOptions;
import com.baidu.mapapi.map.Polyline;
import com.baidu.mapapi.model.LatLng;
import com.baidu.mapapi.search.core.RouteLine;
import com.baidu.mapapi.search.core.SearchResult;
import com.baidu.mapapi.search.geocode.GeoCodeOption;
import com.baidu.mapapi.search.geocode.GeoCodeResult;
import com.baidu.mapapi.search.geocode.GeoCoder;
import com.baidu.mapapi.search.geocode.OnGetGeoCoderResultListener;
import com.baidu.mapapi.search.geocode.ReverseGeoCodeResult;
import com.baidu.mapapi.search.route.BikingRouteResult;
import com.baidu.mapapi.search.route.DrivingRoutePlanOption;
import com.baidu.mapapi.search.route.DrivingRouteResult;
import com.baidu.mapapi.search.route.OnGetRoutePlanResultListener;
import com.baidu.mapapi.search.route.PlanNode;
import com.baidu.mapapi.search.route.RoutePlanSearch;
import com.baidu.mapapi.search.route.TransitRoutePlanOption;
import com.baidu.mapapi.search.route.TransitRouteResult;
import com.baidu.mapapi.search.route.WalkingRoutePlanOption;
import com.baidu.mapapi.search.route.WalkingRouteResult;
import com.baidu.mapapi.utils.DistanceUtil;
import com.baidu.trace.LBSTraceClient;
import com.baidu.trace.OnStartTraceListener;
import com.baidu.trace.Trace;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Timer;
import java.util.TimerTask;

public class MainActivity extends AppCompatActivity implements OnGetRoutePlanResultListener {
    public LocationClient mLocationClient = null;
    private LBSTraceClient LBSTraceclient = null;
    private BDLocationListener myListener = new BDLocationListener() {

        @Override
        public void onReceiveLocation(BDLocation location) {
            // TODO Auto-generated method stub
            if (null != location && location.getLocType() != BDLocation.TypeServerError) {
                StringBuffer sb = new StringBuffer(256);
                sb.append("time : ");
                /**
                 * 时间也可以使用systemClock.elapsedRealtime()方法 获取的是自从开机以来，每次回调的时间；
                 * location.getTime() 是指服务端出本次结果的时间，如果位置不发生变化，则时间不变
                 */

                sb.append(location.getTime());
                sb.append("\nerror code : ");
                sb.append(location.getLocType());
                sb.append("\nlatitude : ");
                sb.append(location.getLatitude());
                GPS_lat = location.getLatitude();
                sb.append("\nlontitude : ");
                sb.append(location.getLongitude());
                GPS_lng = location.getLongitude();
                sb.append("\nradius : ");
                sb.append(location.getRadius());
                sb.append("\nCountryCode : ");
                sb.append(location.getCountryCode());
                sb.append("\nCountry : ");
                sb.append(location.getCountry());
                sb.append("\ncitycode : ");
                sb.append(location.getCityCode());
                sb.append("\ncity : ");
                sb.append(location.getCity());
                sb.append("\nDistrict : ");
                sb.append(location.getDistrict());
                sb.append("\nStreet : ");
                sb.append(location.getStreet());
                sb.append("\naddr : ");
                sb.append(location.getAddrStr());
                sb.append("\nDescribe: ");
                sb.append(location.getLocationDescribe());
                sb.append("\nDirection(not all devices have value): ");
                sb.append(location.getDirection());
                if (location.getLocType() == BDLocation.TypeGpsLocation) {// GPS定位结果
                    sb.append("\nspeed : ");
                    sb.append(location.getSpeed());// 单位：km/h
                    sb.append("\nsatellite : ");
                    sb.append(location.getSatelliteNumber());
                    sb.append("\nheight : ");
                    sb.append(location.getAltitude());// 单位：米
                    sb.append("\ndescribe : ");
                    sb.append("gps定位成功");
                } else if (location.getLocType() == BDLocation.TypeNetWorkLocation) {// 网络定位结果
                    // 运营商信息
                    sb.append("\noperationers : ");
                    sb.append(location.getOperators());
                    sb.append("\ndescribe : ");
                    sb.append("网络定位成功");
                } else if (location.getLocType() == BDLocation.TypeOffLineLocation) {// 离线定位结果
                    sb.append("\ndescribe : ");
                    sb.append("离线定位成功，离线定位结果也是有效的");
                } else if (location.getLocType() == BDLocation.TypeServerError) {
                    sb.append("\ndescribe : ");
                    sb.append("服务端网络定位失败，可以反馈IMEI号和大体定位时间到loc-bugs@baidu.com，会有人追查原因");
                    GPS_Error = 1;
                } else if (location.getLocType() == BDLocation.TypeNetWorkException) {
                    sb.append("\ndescribe : ");
                    sb.append("网络不同导致定位失败，请检查网络是否通畅");
                    GPS_Error = 2;
                } else if (location.getLocType() == BDLocation.TypeCriteriaException) {
                    sb.append("\ndescribe : ");
                    sb.append("无法获取有效定位依据导致定位失败，一般是由于手机的原因，处于飞行模式下一般会造成这种结果，可以试着重启手机");
                    GPS_Error = 3;
                }
                Log.i("Smark_stick",sb.toString());
            }
        }
    };
    private int GPS_Error;
    private MapView mapView = null;
    private Button Analy_Location;
    private double GPS_lat = 23.1558;
    private double GPS_lng = 113.3544;
    private double Analy_lat;
    private double Analy_lng;
    private double Stick_lat = 23.17197;
    private double Stick_lng = 113.3465;
    private String addr;
    private BaiduMap baiduMap;
    private Button Route;
    private Button Lng_Lat_Location;
    private Boolean Search_Type = true;
    private Boolean Address_Search = false;
    private double Distance;
    private boolean Route_Mark = true;
    private ListView Route_listview;
    private ListView Destination_listview;
    private String[] Route_Types = new String[]{
      "公交线路规划","驾车路线规划","步行路线规划","重选目的地"
    };
    private int[] Route_ImageIds = new int[]{
        R.drawable.route_bus,R.drawable.route_car,R.drawable.route_person,R.drawable.route_cancel
    };
    private String[] Destination_Tpyes = new String[]{
      "拐杖所在地","搜索地点所在地","取消路线规划"
    };
    private int[] Destination_ImageIds = new int[]{
            R.drawable.stick_photo,R.drawable.destination,R.drawable.route_cancel
    };
    private int Destination_Point = 0;

    private RouteLine route = null;
    private OverlayManager routeOverlay = null;
    private RoutePlanSearch mSearch = null;
    private boolean Search_Click = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        SDKInitializer.initialize(getApplicationContext());
        setContentView(R.layout.activity_main);
        //获取地图控件引用
        mapView = (MapView) findViewById(R.id.map);
        baiduMap = mapView.getMap();

        //声明LocationClient类
        mLocationClient = new LocationClient(getApplicationContext());
        //注册监听函数
        mLocationClient.registerLocationListener(myListener);
        //初始化定位SDK参数
        initLocation();
        mLocationClient.start();

        //初始化搜索模块，注册事件监听
        mSearch = RoutePlanSearch.newInstance();
        mSearch.setOnGetRoutePlanResultListener(this);

        //实例化轨迹服务客户端
        LBSTraceclient = new LBSTraceClient(getApplicationContext());
        //鹰眼服务ID
        long serviceId = 116616;
        //entity标识
        String entityName = "smark_stick";
        //轨迹服务类型（0 : 不上传位置数据，也不接收报警信息； 1 : 不上传位置数据，但接收报警信息；2 : 上传位置数据，且接收报警信息）
        int  traceType = 1;
        //实例化轨迹服务
        Trace trace = new Trace(getApplicationContext(), serviceId, entityName, traceType);
        //实例化开启轨迹服务回调接口
        OnStartTraceListener startTraceListener = new OnStartTraceListener() {
            //开启轨迹服务回调接口（arg0 : 消息编码，arg1 : 消息内容，详情查看类参考）
            @Override
            public void onTraceCallback(int arg0, String arg1) {
            }
            //轨迹服务推送接口（用于接收服务端推送消息，arg0 : 消息类型，arg1 : 消息内容，详情查看类参考）
            @Override
            public void onTracePushCallback(byte arg0, String arg1) {
            }
        };

        //开启轨迹服务
        LBSTraceclient.startTrace(trace, startTraceListener);

        //实现地图位置调整
        baiduMap.setOnMapLoadedCallback(new BaiduMap.OnMapLoadedCallback() {
            @Override
            public void onMapLoaded() {
                Toast.makeText(MainActivity.this,"地图加载完毕",Toast.LENGTH_SHORT).show();

                final List<OverlayOptions> overlayOptionses = new ArrayList<OverlayOptions>();

                //定义Stick_Maker的坐标点
                LatLng Stick_point = new LatLng(Stick_lat,Stick_lng);
                //构建Stick_Maker图标
                BitmapDescriptor Stick_bitmap = BitmapDescriptorFactory.fromResource(R.drawable.stick_photo);
                //构建Stick_MarkerOption，用于在地图上添加Stick_Maker
                OverlayOptions Stick_option = new MarkerOptions().position(Stick_point).icon(Stick_bitmap);

                //定义GPS_Maker的坐标点
                LatLng GPS_point = new LatLng(GPS_lat,GPS_lng);
                //构建GPS_Maker图标
                BitmapDescriptor GPS_bitmap = BitmapDescriptorFactory.fromResource(R.drawable.gps_photo);
                //构建GPS_MarkerOption，用于在地图上添加GPS_Maker
                OverlayOptions GPS_option = new MarkerOptions().position(GPS_point).icon(GPS_bitmap);

                overlayOptionses.add(Stick_option);
                overlayOptionses.add(GPS_option);

                OverlayManager manager = new OverlayManager(baiduMap) {
                    @Override
                    public List<OverlayOptions> getOverlayOptions() {
                        return overlayOptionses;
                    }

                    @Override
                    public boolean onMarkerClick(Marker marker) {
                        return true;
                    }

                    @Override
                    public boolean onPolylineClick(Polyline polyline) {
                        return false;
                    }
                };

                manager.addToMap();
                manager.zoomToSpan();
            }
        });

        ToggleButton tb = (ToggleButton) findViewById(R.id.tb);
        tb.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    //使用卫星地图
                    baiduMap.setMapType(BaiduMap.MAP_TYPE_NORMAL);
                } else {
                    //设置使用普通地图
                    baiduMap.setMapType(BaiduMap.MAP_TYPE_SATELLITE);
                }
            }
        });

        Analy_Location = (Button) findViewById(R.id.Analy_Location);
        final TextView addrTv = (TextView) findViewById(R.id.address);
        Analy_Location.setOnClickListener(new Button.OnClickListener() {
            @Override
            public void onClick(View v) {
                addr = addrTv.getText().toString();
                if (addr.equals("")) {
                    Toast.makeText(MainActivity.this, "请输入有效的地址", Toast.LENGTH_LONG).show();
                } else {
                    GeoCoder geoCoder = GeoCoder.newInstance();
                    OnGetGeoCoderResultListener listener = new OnGetGeoCoderResultListener() {

                        // 反地理编码查询结果回调函数
                        @Override
                        public void onGetReverseGeoCodeResult(ReverseGeoCodeResult result) {
                            if (result == null
                                    || result.error != SearchResult.ERRORNO.NO_ERROR) {
                                // 没有检测到结果
                                Toast.makeText(MainActivity.this, "抱歉，未能找到结果",
                                        Toast.LENGTH_LONG).show();
                            }
                            Toast.makeText(MainActivity.this,
                                    "位置：" + result.getAddress(), Toast.LENGTH_LONG)
                                    .show();
                        }

                        // 地理编码查询结果回调函数
                        @Override
                        public void onGetGeoCodeResult(GeoCodeResult result) {

                            if (result == null
                                    || result.error != SearchResult.ERRORNO.NO_ERROR) {
                                // 没有检测到结果
                                Toast.makeText(MainActivity.this, "搜索不到正确的位置，请重新输入", Toast.LENGTH_LONG).show();
                            } else {
                                Toast.makeText(MainActivity.this, "纬度：" + result.getLocation().latitude + " 经度：" + result.getLocation().longitude, Toast.LENGTH_LONG).show();
                                //获取地址解析返回的经纬度
                                Analy_lat = result.getLocation().latitude;
                                Analy_lng = result.getLocation().longitude;
                                //表示执行过搜索定位
                                Address_Search = true;
                                //表示执行过地址定位
                                Search_Type = true;
                            }
                        }
                    };
                    // 设置地理编码检索监听者
                    geoCoder.setOnGetGeoCodeResultListener(listener);

                    geoCoder.geocode(new GeoCodeOption().city("广州").address(addr));

                    //geoCoder.destroy();

                }
            }
        });

        Lng_Lat_Location = (Button) findViewById(R.id.Lng_Lat_Location);
        final TextView latTv = (TextView) findViewById(R.id.latitude);
        final TextView lngTv = (TextView) findViewById(R.id.longitude);
        //为经纬度定位按钮添加点击响应时间
        Lng_Lat_Location.setOnClickListener(new Button.OnClickListener() {
            @Override
            public void onClick(View v) {
                //获取用户输入的经纬度
                String lng = lngTv.getEditableText().toString().trim();
                String lat = latTv.getEditableText().toString().trim();
                if (lng.equals("") || lat.equals("")) {
                    Toast.makeText(MainActivity.this, "请输入有效的经度、纬度！", Toast.LENGTH_SHORT).show();
                } else {
                    //将获得的经纬度转为浮点型
                    Analy_lng = Double.parseDouble(lng);
                    Analy_lat = Double.parseDouble(lat);
                    //表示执行过搜索定位
                    Address_Search = true;
                    //表示执行过经纬度定位
                    Search_Type = false;
                }
            }
        });

        Route= (Button) findViewById(R.id.Route_button);
        Route.setOnClickListener(new Button.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(Route_Mark){
                    //此时表示点击了地图路径查询
                    //设置按键不可见
                    Route.setVisibility(View.INVISIBLE);
                    //设置Route_listview不可见
                    Route_listview.setVisibility(View.INVISIBLE);
                    //设置Destination_listview可见
                    Destination_listview.setVisibility(View.VISIBLE);
                    //修改按键的内容
                    Route.setText("消除路线");
                    Route_Mark = false;
                }
                else{
                    //此时代表撤销路线显示
                    //设置按键可见
                    Route.setVisibility(View.VISIBLE);
                    //设置Route_listview不可见
                    Route_listview.setVisibility(View.INVISIBLE);
                    //设置Destination_listview不可见
                    Destination_listview.setVisibility(View.INVISIBLE);
                    //修改按键的内容
                    Route.setText("查询路线");
                    Search_Click = false;
                    Route_Mark = true;
                    UpdatePosition();
                }
            }
        });

        Destination_listview = (ListView) findViewById(R.id.Destination_listView);
        //设定Destination_listview的内容
        List<Map<String,Object>> Destination_listItems = new ArrayList<Map<String,Object>>();
        for (int i=0;i<Destination_Tpyes.length;i++){
            Map<String,Object> listItem = new HashMap<String,Object>();
            listItem.put("header",Destination_ImageIds[i]);
            listItem.put("Route_Type",Destination_Tpyes[i]);
            Destination_listItems.add(listItem);
        }
        //创建一个SimpleAdapter
        SimpleAdapter Destination_simpleAdapter = new SimpleAdapter(this,Destination_listItems,R.layout.simple_item,
                new String[]{"header","Route_Type"},new int[]{R.id.header,R.id.Route_Type});
        //为Destination_listview设置Adapter
        Destination_listview.setAdapter(Destination_simpleAdapter);

        //为Destination_listview的列表项的单击事件绑定事件监听器
        Destination_listview.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                //设置按键不可见
                Route.setVisibility(View.INVISIBLE);
                //设置Route_listview可见
                Route_listview.setVisibility(View.VISIBLE);
                //设置Destination_listview不可见
                Destination_listview.setVisibility(View.INVISIBLE);

                if(position==0){
                    Toast.makeText(MainActivity.this,Destination_Tpyes[position],Toast.LENGTH_SHORT).show();
                    //此时将路线规划的目的地点设为拐杖所在地
                    Destination_Point = 0;
                }
                else {
                    if(position==1){
                        Toast.makeText(MainActivity.this,Destination_Tpyes[position],Toast.LENGTH_SHORT).show();
                        //此时将路线规划的目的地设为搜索得到的地址
                        Destination_Point = 1;
                    }
                    else {
                        //此时执行的是返回操作
                        //修改按键的内容
                        Route.setText("查询路线");
                        //设置按键可见
                        Route.setVisibility(View.VISIBLE);
                        //设置Route_listview不可见
                        Route_listview.setVisibility(View.INVISIBLE);
                        Toast.makeText(MainActivity.this,Destination_Tpyes[position],Toast.LENGTH_SHORT).show();
                        Route_Mark = true;
                        UpdatePosition();
                    }
                }
            }
        });

        Route_listview = (ListView) findViewById(R.id.Route_listView);
        //设定Route_listview的内容
        List<Map<String,Object>> listItems = new ArrayList<Map<String,Object>>();
        for (int i=0;i<Route_Types.length;i++){
            Map<String,Object> listItem = new HashMap<String,Object>();
            listItem.put("header",Route_ImageIds[i]);
            listItem.put("Route_Type",Route_Types[i]);
            listItems.add(listItem);
        }
        //创建一个SimpleAdapter
        SimpleAdapter simpleAdapter = new SimpleAdapter(this,listItems,R.layout.simple_item,
                new String[]{"header","Route_Type"},new int[]{R.id.header,R.id.Route_Type});
        //为Route_listview设置Adapter
        Route_listview.setAdapter(simpleAdapter);

        //为Route_listview的列表项的单击事件绑定事件监听器
        Route_listview.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                //设置路线按钮可见
                Route.setVisibility(View.VISIBLE);
                //设置Route_listview不可见
                Route_listview.setVisibility(View.INVISIBLE);
                //设置Destination_listview不可见
                Destination_listview.setVisibility(View.INVISIBLE);

                //当点击了公交路线规划时的响应
                if(position==0){
                    //设置进行过路线规划
                    Search_Click = true;

                    LatLng GPS_start = new LatLng(GPS_lat,GPS_lng);
                    if(Destination_Point==0){
                        Toast.makeText(MainActivity.this,Route_Types[position],Toast.LENGTH_SHORT).show();
                        //此时代表目的地是拐杖所在地
                        LatLng end_point = new LatLng(Stick_lat,Stick_lng);

                        //设置起点与终点
                        PlanNode stNode = PlanNode.withLocation(GPS_start);
                        PlanNode enNode = PlanNode.withLocation(end_point);

                        //发起路线规划搜索实例
                        mSearch.transitSearch((new TransitRoutePlanOption()).from(stNode).to(enNode).city("广州"));
                    }
                    else {
                        if(Destination_Point==1){
                            //此时代表目的地是搜索地点所在地
                            if(Address_Search){
                                Toast.makeText(MainActivity.this,Route_Types[position],Toast.LENGTH_SHORT).show();
                                //此时代表进行过地址搜索
                                LatLng end_point = new LatLng(Analy_lat,Analy_lng);

                                //设置起点与终点
                                PlanNode stNode = PlanNode.withLocation(GPS_start);
                                PlanNode enNode = PlanNode.withLocation(end_point);

                                //发起路线规划搜索实例
                                mSearch.transitSearch((new TransitRoutePlanOption()).from(stNode).to(enNode).city("广州"));
                            }
                            else {
                                Toast.makeText(MainActivity.this,"请输入目的地后再进行路线规划",Toast.LENGTH_SHORT).show();
                                //修改按键的内容
                                Route.setText("查询路线");
                                Route_Mark = true;
                                //重新开启地图界面更新
                                Search_Click = false;
                            }
                        }
                    }
                }

                //当点击了驾车路线规划时的响应
                if(position==1){
                    //设置进行过路线规划
                    Search_Click = true;

                    LatLng GPS_start = new LatLng(GPS_lat,GPS_lng);
                    if(Destination_Point==0){
                        Toast.makeText(MainActivity.this,Route_Types[position],Toast.LENGTH_SHORT).show();
                        //此时代表目的地是拐杖所在地
                        LatLng end_point = new LatLng(Stick_lat,Stick_lng);

                        //设置起点与终点
                        PlanNode stNode = PlanNode.withLocation(GPS_start);
                        PlanNode enNode = PlanNode.withLocation(end_point);

                        //发起路线规划搜索实例
                        mSearch.drivingSearch((new DrivingRoutePlanOption()).from(stNode).to(enNode));
                    }
                    else {
                        if(Destination_Point==1){
                            //此时代表目的地是搜索地点所在地
                            if(Address_Search){
                                Toast.makeText(MainActivity.this,Route_Types[position],Toast.LENGTH_SHORT).show();
                                //此时代表的是进行过地址搜索
                                LatLng end_point = new LatLng(Analy_lat,Analy_lng);

                                //设置起点与终点
                                PlanNode stNode = PlanNode.withLocation(GPS_start);
                                PlanNode enNode = PlanNode.withLocation(end_point);

                                //发起路线规划搜索实例
                                mSearch.drivingSearch((new DrivingRoutePlanOption()).from(stNode).to(enNode));
                            }
                            else {
                                Toast.makeText(MainActivity.this,"请输入目的地后再进行路线规划",Toast.LENGTH_SHORT).show();
                                //修改按键的内容
                                Route.setText("查询路线");
                                Route_Mark = true;
                                //重新开启地图界面更新
                                Search_Click = false;
                            }
                        }
                    }
                }

                //当点击了步行路线规划时的响应
                if(position==2){
                    //设置进行过路线规划
                    Search_Click = true;

                    LatLng GPS_start = new LatLng(GPS_lat,GPS_lng);
                    if(Destination_Point==0){
                        Toast.makeText(MainActivity.this,Route_Types[position],Toast.LENGTH_SHORT).show();
                        //此时代表目的地是拐杖所在地
                        LatLng end_point = new LatLng(Stick_lat,Stick_lng);

                        //设置起点与终点
                        PlanNode stNode = PlanNode.withLocation(GPS_start);
                        PlanNode enNode = PlanNode.withLocation(end_point);

                        //发起路线规划搜索实例
                        mSearch.walkingSearch((new WalkingRoutePlanOption()).from(stNode).to(enNode));
                    }
                    else {
                        if(Destination_Point==1){
                            //此时代表目的地是搜索地点所在地
                            if(Address_Search){
                                Toast.makeText(MainActivity.this,Route_Types[position],Toast.LENGTH_SHORT).show();
                                //此时代表的是进行过地址搜索
                                LatLng end_point = new LatLng(Analy_lat,Analy_lng);

                                //设置起点与终点
                                PlanNode stNode = PlanNode.withLocation(GPS_start);
                                PlanNode enNode = PlanNode.withLocation(end_point);

                                //发起路线规划搜索实例
                                mSearch.walkingSearch((new WalkingRoutePlanOption()).from(stNode).to(enNode));
                            }
                            else {
                                Toast.makeText(MainActivity.this,"请输入目的地后再进行路线规划",Toast.LENGTH_SHORT).show();
                                //修改按键的内容
                                Route.setText("查询路线");
                                Route_Mark = true;
                                //重新开启地图界面更新
                                Search_Click = false;
                            }
                        }
                    }
                }

                //当点击了返回目的地设定的响应
                if(position==3){
                    Toast.makeText(MainActivity.this,Route_Types[position],Toast.LENGTH_SHORT).show();
                    //设置路线按钮不可见
                    Route.setVisibility(View.INVISIBLE);
                    //设置Destination_listview可见
                    Destination_listview.setVisibility(View.VISIBLE);
                }
            }
        });


        final Handler handler = new Handler(){
            @Override
            public void handleMessage(Message msg) {
                if(msg.what==0x123){
                    UpdatePosition();
                }
                if(msg.what==0x1234){
                    Toast.makeText(MainActivity.this,"距离："+Distance+"超出预设范围，请注意老人安全",Toast.LENGTH_SHORT).show();
                }
            }
        };

        //开启一个新的线程用于更新地图上面GPS的位置
        final Timer timer = new Timer();
        timer.schedule(new TimerTask() {
            @Override
            public void run() {
                if(Search_Click){

                }
                else {
                    handler.sendEmptyMessage(0x123);
                }
            }
        },0,1500);

        //开启一个新的线程用于计算拐杖与手机的距离，以免拐杖距离手机距离过远时发出提醒
        final Timer timer1 = new Timer();
        timer1.schedule(new TimerTask() {
            @Override
            public void run() {

                //获取计算拐杖与手机的距离
                LatLng GPS_point = new LatLng(GPS_lat,GPS_lng);
                LatLng Stick_point = new LatLng(Stick_lat,Stick_lng);

                Distance = DistanceUtil.getDistance(GPS_point,Stick_point);

                //老人超出预设范围，提醒用户注意老人安全
                if(Distance>=3000)
                {
                    handler.sendEmptyMessage(0x1234);
                }
            }
        },0,60000);
    }

    private void UpdatePosition() {
        baiduMap.clear();

        //定义GPS_Maker的坐标点
        LatLng GPS_point = new LatLng(GPS_lat,GPS_lng);
        //构建GPS_Maker图标
        BitmapDescriptor GPS_bitmap = BitmapDescriptorFactory.fromResource(R.drawable.gps_photo);
        //构建GPS_MarkerOption，用于在地图上添加GPS_Maker
        OverlayOptions GPS_option = new MarkerOptions().position(GPS_point).icon(GPS_bitmap);
        //在地图上添加GPS_Maker并显示
        baiduMap.addOverlay(GPS_option);

        //定义Stick_Maker的坐标点
        LatLng Stick_point = new LatLng(Stick_lat,Stick_lng);
        //构建Stick_Maker图标
        BitmapDescriptor Stick_bitmap = BitmapDescriptorFactory.fromResource(R.drawable.stick_photo);
        //构建Stick_MarkerOption，用于在地图上添加Stick_Maker
        OverlayOptions Stick_option = new MarkerOptions().position(Stick_point).icon(Stick_bitmap);
        //在地图上添加Stick_Maker并显示
        baiduMap.addOverlay(Stick_option);

        //定义Search_Maker的坐标点
        LatLng Search_point = new LatLng(Analy_lat,Analy_lng);
        //构建Search_Maker图标
        BitmapDescriptor Search_bitmap = BitmapDescriptorFactory.fromResource(R.drawable.search_photo);
        //构建Search_MarkerOption，用于在地图上添加Search_Maker
        OverlayOptions Search_option = new MarkerOptions().position(Search_point).icon(Search_bitmap);
        if(Address_Search){
            //执行过搜索定位
            TextView textView = new TextView(getApplicationContext());
            if(Search_Type){
                textView.setText("地址解析定位结果");
                textView.setTextColor(Color.BLACK);
                baiduMap.addOverlay(Search_option);
                InfoWindow mInfoWindow = new InfoWindow(textView,Search_point,-80);
                baiduMap.showInfoWindow(mInfoWindow);
            }
            else {
                textView.setText("经纬度定位结果");
                textView.setTextColor(Color.BLACK);
                baiduMap.addOverlay(Search_option);
                InfoWindow mInfoWindow = new InfoWindow(textView,Search_point,-80);
                baiduMap.showInfoWindow(mInfoWindow);
            }
        }
    }


    private void initLocation() {
        LocationClientOption option = new LocationClientOption();
        //设置定位模式为高精度定位
        option.setLocationMode(LocationClientOption.LocationMode.Hight_Accuracy);

        //设置定位间隔
        int span = 3000;
        option.setScanSpan(span);

        //设置是否使用gps
        option.setOpenGps(true);

        //设置是否需要POI
        option.setIsNeedLocationPoiList(true);

        mLocationClient.setLocOption(option);
    }


    protected void onPause() {
        super.onPause();
        mapView.onPause();
    }

    protected void onDestroy() {
        super.onDestroy();
        mapView.onDestroy();
    }

    protected void onResume() {
        super.onResume();
        mapView.onResume();
    }

    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);
    }

    @Override
    public void onGetWalkingRouteResult(WalkingRouteResult walkingRouteResult) {
        if (walkingRouteResult == null || walkingRouteResult.error != SearchResult.ERRORNO.NO_ERROR) {
            Toast.makeText(MainActivity.this, "抱歉，未找到结果", Toast.LENGTH_SHORT).show();
        }
        if (walkingRouteResult.error == SearchResult.ERRORNO.AMBIGUOUS_ROURE_ADDR) {
            // 起终点或途经点地址有岐义，通过以下接口获取建议查询信息
            // result.getSuggestAddrInfo()
            Toast.makeText(MainActivity.this, "起终点或途经点地址有岐义", Toast.LENGTH_SHORT).show();
            return;
        }
        if (walkingRouteResult.error == SearchResult.ERRORNO.NO_ERROR) {
            route = walkingRouteResult.getRouteLines().get(0);
            WalkingRouteOverlay overlay = new MyWalkingRouteOverlay(baiduMap);
            routeOverlay = overlay;
            baiduMap.setOnMarkerClickListener(overlay);
            overlay.setData(walkingRouteResult.getRouteLines().get(0));
            overlay.addToMap();
            overlay.zoomToSpan();
        }
    }

    @Override
    public void onGetTransitRouteResult(TransitRouteResult result) {

        if (result == null || result.error != SearchResult.ERRORNO.NO_ERROR) {
            Toast.makeText(MainActivity.this, "抱歉，未找到结果", Toast.LENGTH_SHORT).show();
        }
        if (result.error == SearchResult.ERRORNO.AMBIGUOUS_ROURE_ADDR) {
            // 起终点或途经点地址有岐义，通过以下接口获取建议查询信息
            // result.getSuggestAddrInfo()
            Toast.makeText(MainActivity.this, "起终点或途经点地址有岐义", Toast.LENGTH_SHORT).show();
            return;
        }
        if (result.error == SearchResult.ERRORNO.NO_ERROR) {
            route = result.getRouteLines().get(0);
            TransitRouteOverlay overlay = new MyTransitRouteOverlay(baiduMap);
            routeOverlay = overlay;
            baiduMap.setOnMarkerClickListener(overlay);
            overlay.setData(result.getRouteLines().get(0));
            overlay.addToMap();
            overlay.zoomToSpan();
        }
    }

    @Override
    public void onGetDrivingRouteResult(DrivingRouteResult drivingRouteResult) {
        if (drivingRouteResult == null || drivingRouteResult.error != SearchResult.ERRORNO.NO_ERROR) {
            Toast.makeText(MainActivity.this, "抱歉，未找到结果", Toast.LENGTH_SHORT).show();
        }
        if (drivingRouteResult.error == SearchResult.ERRORNO.AMBIGUOUS_ROURE_ADDR) {
            // 起终点或途经点地址有岐义，通过以下接口获取建议查询信息
            // result.getSuggestAddrInfo()
            Toast.makeText(MainActivity.this, "起终点或途经点地址有岐义", Toast.LENGTH_SHORT).show();
            return;
        }
        if (drivingRouteResult.error == SearchResult.ERRORNO.NO_ERROR) {
            route = drivingRouteResult.getRouteLines().get(0);
            DrivingRouteOverlay overlay = new MyDrivingRouteOverlay(baiduMap);
            routeOverlay = overlay;
            baiduMap.setOnMarkerClickListener(overlay);
            overlay.setData(drivingRouteResult.getRouteLines().get(0));
            overlay.addToMap();
            overlay.zoomToSpan();
        }
    }

    @Override
    public void onGetBikingRouteResult(BikingRouteResult bikingRouteResult) {

    }

    private class MyTransitRouteOverlay extends TransitRouteOverlay {
        public MyTransitRouteOverlay(BaiduMap baiduMap) {
            super(baiduMap);
        }
    }

    private class MyWalkingRouteOverlay extends WalkingRouteOverlay{
        public MyWalkingRouteOverlay(BaiduMap baiduMap) {
            super(baiduMap);
        }
    }

    private class MyDrivingRouteOverlay extends DrivingRouteOverlay {
        public MyDrivingRouteOverlay(BaiduMap baiduMap) {
            super(baiduMap);
        }
    }
}
