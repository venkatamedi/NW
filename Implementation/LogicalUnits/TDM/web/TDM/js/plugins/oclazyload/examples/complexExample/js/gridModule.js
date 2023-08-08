angular.module("gridModule",[{name:"ui.grid.rowEdit",files:["js/plugins/angular-ui-grid/ui-grid.js","js/plugins/angular-ui-grid/ui-grid.css"]}]).controller("GridModuleCtrl",(function($scope){console.log("-------  grid module ctrl"),$scope.myData=[{name:"Moroni",age:50},{name:"Teancum",age:43},{name:"Jacob",age:27},{name:"Nephi",age:29},{name:"Enos",age:34}],$scope.gridOptions={data:"myData"}})).config((function(){console.warn("config gridModule")})).config(["$ocLazyLoadProvider",function($ocLazyLoadProvider){console.warn("config 2 gridModule")}]).run((function(){console.warn("run gridModule")}));
//# sourceMappingURL=data:application/json;charset=utf8;base64,eyJ2ZXJzaW9uIjozLCJzb3VyY2VzIjpbInBsdWdpbnMvb2NsYXp5bG9hZC9leGFtcGxlcy9jb21wbGV4RXhhbXBsZS9qcy9ncmlkTW9kdWxlLmpzIl0sIm5hbWVzIjpbImFuZ3VsYXIiLCJtb2R1bGUiLCJuYW1lIiwiZmlsZXMiLCJjb250cm9sbGVyIiwiJHNjb3BlIiwiY29uc29sZSIsImxvZyIsIm15RGF0YSIsImFnZSIsImdyaWRPcHRpb25zIiwiZGF0YSIsImNvbmZpZyIsIndhcm4iLCIkb2NMYXp5TG9hZFByb3ZpZGVyIiwicnVuIl0sIm1hcHBpbmdzIjoiQUFDQUEsUUFBUUMsT0FBTyxhQUFjLENBQUMsQ0FDNUJDLEtBQU0sa0JBQW1CQyxNQUFPLENBQzlCLHdDQUNBLDZDQUVBQyxXQUFXLGtCQUFrQixTQUFTQyxRQUN4Q0MsUUFBUUMsSUFBSSw2QkFDWkYsT0FBT0csT0FBUyxDQUFDLENBQUNOLEtBQU0sU0FBVU8sSUFBSyxJQUNyQyxDQUFDUCxLQUFNLFVBQVdPLElBQUssSUFDdkIsQ0FBQ1AsS0FBTSxRQUFTTyxJQUFLLElBQ3JCLENBQUNQLEtBQU0sUUFBU08sSUFBSyxJQUNyQixDQUFDUCxLQUFNLE9BQVFPLElBQUssS0FDdEJKLE9BQU9LLFlBQWMsQ0FBQ0MsS0FBTSxhQUMzQkMsUUFBTyxXQUNSTixRQUFRTyxLQUFLLHdCQUNaRCxPQUFPLENBQUMsc0JBQXVCLFNBQVNFLHFCQUN6Q1IsUUFBUU8sS0FBSywwQkFDWEUsS0FBSSxXQUNOVCxRQUFRTyxLQUFLIiwiZmlsZSI6InBsdWdpbnMvb2NsYXp5bG9hZC9leGFtcGxlcy9jb21wbGV4RXhhbXBsZS9qcy9ncmlkTW9kdWxlLmpzIiwic291cmNlc0NvbnRlbnQiOlsiLy8gbmdHcmlkIGlzIGFsc28gbGF6eSBsb2FkZWQgYnkgJG9jTGF6eUxvYWQgdGhhbmtzIHRvIHRoZSBtb2R1bGUgZGVwZW5kZW5jeSBpbmplY3Rpb24gIVxuYW5ndWxhci5tb2R1bGUoJ2dyaWRNb2R1bGUnLCBbe1xuICBuYW1lOiAndWkuZ3JpZC5yb3dFZGl0JywgZmlsZXM6IFtcbiAgICAnanMvcGx1Z2lucy9hbmd1bGFyLXVpLWdyaWQvdWktZ3JpZC5qcycsXG4gICAgJ2pzL3BsdWdpbnMvYW5ndWxhci11aS1ncmlkL3VpLWdyaWQuY3NzJ1xuICBdXG59XSkuY29udHJvbGxlcignR3JpZE1vZHVsZUN0cmwnLCBmdW5jdGlvbigkc2NvcGUpIHtcbiAgY29uc29sZS5sb2coJy0tLS0tLS0gIGdyaWQgbW9kdWxlIGN0cmwnKTtcbiAgJHNjb3BlLm15RGF0YSA9IFt7bmFtZTogXCJNb3JvbmlcIiwgYWdlOiA1MH0sXG4gICAge25hbWU6IFwiVGVhbmN1bVwiLCBhZ2U6IDQzfSxcbiAgICB7bmFtZTogXCJKYWNvYlwiLCBhZ2U6IDI3fSxcbiAgICB7bmFtZTogXCJOZXBoaVwiLCBhZ2U6IDI5fSxcbiAgICB7bmFtZTogXCJFbm9zXCIsIGFnZTogMzR9XTtcbiAgJHNjb3BlLmdyaWRPcHRpb25zID0ge2RhdGE6ICdteURhdGEnfTtcbn0pLmNvbmZpZyhmdW5jdGlvbigpIHtcbiAgY29uc29sZS53YXJuKCdjb25maWcgZ3JpZE1vZHVsZScpO1xufSkuY29uZmlnKFsnJG9jTGF6eUxvYWRQcm92aWRlcicsIGZ1bmN0aW9uKCRvY0xhenlMb2FkUHJvdmlkZXIpIHtcbiAgY29uc29sZS53YXJuKCdjb25maWcgMiBncmlkTW9kdWxlJyk7XG59XSkucnVuKGZ1bmN0aW9uKCkge1xuICBjb25zb2xlLndhcm4oJ3J1biBncmlkTW9kdWxlJyk7XG59KTtcbiJdfQ==