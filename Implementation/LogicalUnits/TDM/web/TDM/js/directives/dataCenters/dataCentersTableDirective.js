function centersDataTableDirective(){return{restrict:"E",templateUrl:"views/dataCenters/dataCentersTable.html",scope:{content:"="},controller:function($scope,$compile,TDMService,DTColumnBuilder,DTOptionsBuilder,$q,$timeout){var dataCentersTableCtrl=this;dataCentersTableCtrl.loadingTable=!0,TDMService.getDataCenters().then((function(response){if("SUCCESS"==response.errorCode){dataCentersTableCtrl.dataCentersData=response.result,dataCentersTableCtrl.dtInstance={},dataCentersTableCtrl.dtColumns=[],dataCentersTableCtrl.dtColumnDefs=[],dataCentersTableCtrl.headers=[{column:"dc",name:"Name",clickAble:!0},{column:"effective_ip",name:"IP Address",clickAble:!0},{column:"node_id",name:"Node Id",clickAble:!0},{column:"notes",name:"Description",clickAble:!1},{column:"status",name:"Status",clickAble:!1}];for(var changeToLocalDate=function(data,type,full,meta){return moment(data).format("DD MMM YYYY, HH:mm")},i=0;i<dataCentersTableCtrl.headers.length;i++)"date"==dataCentersTableCtrl.headers[i].type?dataCentersTableCtrl.dtColumns.push(DTColumnBuilder.newColumn(dataCentersTableCtrl.headers[i].column).withTitle(dataCentersTableCtrl.headers[i].name).renderWith(changeToLocalDate)):dataCentersTableCtrl.dtColumns.push(DTColumnBuilder.newColumn(dataCentersTableCtrl.headers[i].column).withTitle(dataCentersTableCtrl.headers[i].name));var getTableData=function(){var deferred=$q.defer();return deferred.resolve(dataCentersTableCtrl.dataCentersData),deferred.promise};dataCentersTableCtrl.dtOptions=DTOptionsBuilder.fromFnPromise((function(){return getTableData()})).withDOM('<"html5buttons"B>lTfgitp').withOption("createdRow",(function(row){$compile(angular.element(row).contents())($scope)})).withOption("scrollX",!1).withButtons([]).withOption("search",{caseInsensitive:!0,useWildcards:!1}),dataCentersTableCtrl.dataCentersData&&dataCentersTableCtrl.dataCentersData.length>0&&dataCentersTableCtrl.dtOptions.withLightColumnFilter({0:{type:"text"},1:{type:"text"},2:{type:"text"},3:{type:"text"},4:{type:"text"}}),dataCentersTableCtrl.dtInstanceCallback=function(dtInstance){angular.isFunction(dataCentersTableCtrl.dtInstance)?dataCentersTableCtrl.dtInstance(dtInstance):angular.isDefined(dataCentersTableCtrl.dtInstance)&&(dataCentersTableCtrl.dtInstance=dtInstance)},null!=dataCentersTableCtrl.dtInstance.changeData&&dataCentersTableCtrl.dtInstance.changeData(getTableData()),$timeout(()=>{dataCentersTableCtrl.loadingTable=!1})}})),dataCentersTableCtrl.openDataCenter=function(data_center_id){if($scope.content.openDataCenter){var dataCenterData=_.find(dataCentersTableCtrl.dataCentersData,{data_center_id:data_center_id});if(dataCenterData)return void $scope.content.openDataCenter(dataCenterData)}},dataCentersTableCtrl.openNewDataCenter=function(){$scope.content.openNewDataCenter&&$scope.content.openNewDataCenter(dataCentersTableCtrl.dataCentersData)}},controllerAs:"dataCentersTableCtrl"}}angular.module("TDM-FE").directive("centersDataTableDirective",centersDataTableDirective);
//# sourceMappingURL=data:application/json;charset=utf8;base64,eyJ2ZXJzaW9uIjozLCJzb3VyY2VzIjpbImRpcmVjdGl2ZXMvZGF0YUNlbnRlcnMvZGF0YUNlbnRlcnNUYWJsZURpcmVjdGl2ZS5qcyJdLCJuYW1lcyI6WyJjZW50ZXJzRGF0YVRhYmxlRGlyZWN0aXZlIiwicmVzdHJpY3QiLCJ0ZW1wbGF0ZVVybCIsInNjb3BlIiwiY29udGVudCIsImNvbnRyb2xsZXIiLCIkc2NvcGUiLCIkY29tcGlsZSIsIlRETVNlcnZpY2UiLCJEVENvbHVtbkJ1aWxkZXIiLCJEVE9wdGlvbnNCdWlsZGVyIiwiJHEiLCIkdGltZW91dCIsImRhdGFDZW50ZXJzVGFibGVDdHJsIiwidGhpcyIsImxvYWRpbmdUYWJsZSIsImdldERhdGFDZW50ZXJzIiwidGhlbiIsInJlc3BvbnNlIiwiZXJyb3JDb2RlIiwiZGF0YUNlbnRlcnNEYXRhIiwicmVzdWx0IiwiZHRJbnN0YW5jZSIsImR0Q29sdW1ucyIsImR0Q29sdW1uRGVmcyIsImhlYWRlcnMiLCJjb2x1bW4iLCJuYW1lIiwiY2xpY2tBYmxlIiwiY2hhbmdlVG9Mb2NhbERhdGUiLCJkYXRhIiwidHlwZSIsImZ1bGwiLCJtZXRhIiwibW9tZW50IiwiZm9ybWF0IiwiaSIsImxlbmd0aCIsInB1c2giLCJuZXdDb2x1bW4iLCJ3aXRoVGl0bGUiLCJyZW5kZXJXaXRoIiwiZ2V0VGFibGVEYXRhIiwiZGVmZXJyZWQiLCJkZWZlciIsInJlc29sdmUiLCJwcm9taXNlIiwiZHRPcHRpb25zIiwiZnJvbUZuUHJvbWlzZSIsIndpdGhET00iLCJ3aXRoT3B0aW9uIiwicm93IiwiYW5ndWxhciIsImVsZW1lbnQiLCJjb250ZW50cyIsIndpdGhCdXR0b25zIiwiY2FzZUluc2Vuc2l0aXZlIiwidXNlV2lsZGNhcmRzIiwid2l0aExpZ2h0Q29sdW1uRmlsdGVyIiwiMCIsIjEiLCIyIiwiMyIsIjQiLCJkdEluc3RhbmNlQ2FsbGJhY2siLCJpc0Z1bmN0aW9uIiwiaXNEZWZpbmVkIiwiY2hhbmdlRGF0YSIsIm9wZW5EYXRhQ2VudGVyIiwiZGF0YV9jZW50ZXJfaWQiLCJkYXRhQ2VudGVyRGF0YSIsIl8iLCJmaW5kIiwib3Blbk5ld0RhdGFDZW50ZXIiLCJjb250cm9sbGVyQXMiLCJtb2R1bGUiLCJkaXJlY3RpdmUiXSwibWFwcGluZ3MiOiJBQUFBLFNBQVNBLDRCQThJTCxNQUFPLENBQ0hDLFNBQVUsSUFDVkMsWUE5SVcsMENBK0lYQyxNQUFPLENBQ0hDLFFBQVMsS0FFYkMsV0FoSmEsU0FBVUMsT0FBT0MsU0FBU0MsV0FBV0MsZ0JBQWdCQyxpQkFBaUJDLEdBQUdDLFVBQ3RGLElBQUlDLHFCQUF1QkMsS0FFM0JELHFCQUFxQkUsY0FBZSxFQUVwQ1AsV0FBV1EsaUJBQWlCQyxNQUFLLFNBQVNDLFVBQ3RDLEdBQTBCLFdBQXRCQSxTQUFTQyxVQUFiLENBSUFOLHFCQUFxQk8sZ0JBQWtCRixTQUFTRyxPQUNoRFIscUJBQXFCUyxXQUFhLEdBQ2xDVCxxQkFBcUJVLFVBQVksR0FDakNWLHFCQUFxQlcsYUFBZSxHQUNwQ1gscUJBQXFCWSxRQUFVLENBQzNCLENBQ0lDLE9BQVMsS0FDVEMsS0FBTyxPQUNQQyxXQUFZLEdBRWhCLENBQ0lGLE9BQVMsZUFDVEMsS0FBTyxhQUNQQyxXQUFZLEdBRWhCLENBQ0lGLE9BQVMsVUFDVEMsS0FBTyxVQUNQQyxXQUFZLEdBRWhCLENBQ0lGLE9BQVMsUUFDVEMsS0FBTyxjQUNQQyxXQUFZLEdBRWhCLENBQ0lGLE9BQVMsU0FDVEMsS0FBTyxTQUNQQyxXQUFZLElBYXBCLElBVEEsSUFJSUMsa0JBQW9CLFNBQVNDLEtBQU1DLEtBQU1DLEtBQU1DLE1BQy9DLE9BQU9DLE9BQU9KLE1BQU1LLE9BQU8sdUJBSXRCQyxFQUFJLEVBQUdBLEVBQUt2QixxQkFBcUJZLFFBQVFZLE9BQVNELElBQ1gsUUFBeEN2QixxQkFBcUJZLFFBQVFXLEdBQUdMLEtBQ2hDbEIscUJBQXFCVSxVQUFVZSxLQUFLN0IsZ0JBQWdCOEIsVUFBVTFCLHFCQUFxQlksUUFBUVcsR0FBR1YsUUFBUWMsVUFBVTNCLHFCQUFxQlksUUFBUVcsR0FBR1QsTUFBTWMsV0FBV1osb0JBR2pLaEIscUJBQXFCVSxVQUFVZSxLQUFLN0IsZ0JBQWdCOEIsVUFBVTFCLHFCQUFxQlksUUFBUVcsR0FBR1YsUUFBUWMsVUFBVTNCLHFCQUFxQlksUUFBUVcsR0FBR1QsT0FJeEosSUFBSWUsYUFBZSxXQUNmLElBQUlDLFNBQVdoQyxHQUFHaUMsUUFFbEIsT0FEQUQsU0FBU0UsUUFBUWhDLHFCQUFxQk8saUJBQy9CdUIsU0FBU0csU0FHcEJqQyxxQkFBcUJrQyxVQUFZckMsaUJBQWlCc0MsZUFBYyxXQUN4RCxPQUFPTixrQkFFVk8sUUFBUSw0QkFDUkMsV0FBVyxjQUFjLFNBQVVDLEtBRWhDNUMsU0FBUzZDLFFBQVFDLFFBQVFGLEtBQUtHLFdBQTlCL0MsQ0FBMENELFdBRTdDNEMsV0FBVyxXQUFXLEdBQ3RCSyxZQUFZLElBRVpMLFdBQVcsU0FBUyxDQUNqQk0saUJBQW1CLEVBQ25CQyxjQUFpQixJQUdqQjVDLHFCQUFxQk8saUJBQW1CUCxxQkFBcUJPLGdCQUFnQmlCLE9BQVMsR0FDdEZ4QixxQkFBcUJrQyxVQUFVVyxzQkFBc0IsQ0FDN0NDLEVBQUksQ0FDQTVCLEtBQU0sUUFFVjZCLEVBQUksQ0FDQTdCLEtBQU0sUUFFVjhCLEVBQUksQ0FDQTlCLEtBQU0sUUFFVitCLEVBQUksQ0FDQS9CLEtBQU0sUUFFVmdDLEVBQUksQ0FDQWhDLEtBQU0sVUFLMUJsQixxQkFBcUJtRCxtQkFBcUIsU0FBVTFDLFlBQzVDOEIsUUFBUWEsV0FBV3BELHFCQUFxQlMsWUFDeENULHFCQUFxQlMsV0FBV0EsWUFDekI4QixRQUFRYyxVQUFVckQscUJBQXFCUyxjQUM5Q1QscUJBQXFCUyxXQUFhQSxhQUdRLE1BQTlDVCxxQkFBcUJTLFdBQVc2QyxZQUNoQ3RELHFCQUFxQlMsV0FBVzZDLFdBQVd6QixnQkFFM0M5QixTQUFTLEtBQ0xDLHFCQUFxQkUsY0FBZSxRQUtoREYscUJBQXFCdUQsZUFBaUIsU0FBU0MsZ0JBQzNDLEdBQUkvRCxPQUFPRixRQUFRZ0UsZUFBZ0IsQ0FDL0IsSUFBSUUsZUFBaUJDLEVBQUVDLEtBQUszRCxxQkFBcUJPLGdCQUFpQixDQUFDaUQsZUFBZ0JBLGlCQUNuRixHQUFJQyxlQUVBLFlBREFoRSxPQUFPRixRQUFRZ0UsZUFBZUUsa0JBTzFDekQscUJBQXFCNEQsa0JBQW9CLFdBQ2pDbkUsT0FBT0YsUUFBUXFFLG1CQUNmbkUsT0FBT0YsUUFBUXFFLGtCQUFrQjVELHFCQUFxQk8sbUJBYzlEc0QsYUFBYyx3QkFLdEJ0QixRQUNLdUIsT0FBTyxVQUNQQyxVQUFVLDRCQUE2QjVFIiwiZmlsZSI6ImRpcmVjdGl2ZXMvZGF0YUNlbnRlcnMvZGF0YUNlbnRlcnNUYWJsZURpcmVjdGl2ZS5qcyIsInNvdXJjZXNDb250ZW50IjpbImZ1bmN0aW9uIGNlbnRlcnNEYXRhVGFibGVEaXJlY3RpdmUoKXtcblxuICAgIHZhciB0ZW1wbGF0ZSA9IFwidmlld3MvZGF0YUNlbnRlcnMvZGF0YUNlbnRlcnNUYWJsZS5odG1sXCI7XG5cbiAgICB2YXIgY29udHJvbGxlciA9IGZ1bmN0aW9uICgkc2NvcGUsJGNvbXBpbGUsVERNU2VydmljZSxEVENvbHVtbkJ1aWxkZXIsRFRPcHRpb25zQnVpbGRlciwkcSwkdGltZW91dCkge1xuICAgICAgICB2YXIgZGF0YUNlbnRlcnNUYWJsZUN0cmwgPSB0aGlzO1xuXG4gICAgICAgIGRhdGFDZW50ZXJzVGFibGVDdHJsLmxvYWRpbmdUYWJsZSA9IHRydWU7XG5cbiAgICAgICAgVERNU2VydmljZS5nZXREYXRhQ2VudGVycygpLnRoZW4oZnVuY3Rpb24ocmVzcG9uc2Upe1xuICAgICAgICAgICAgaWYgKHJlc3BvbnNlLmVycm9yQ29kZSAhPSAnU1VDQ0VTUycpe1xuICAgICAgICAgICAgICAgIC8vVE9ETyBzaG93IEVycm9yXG4gICAgICAgICAgICAgICAgcmV0dXJuO1xuICAgICAgICAgICAgfVxuICAgICAgICAgICAgZGF0YUNlbnRlcnNUYWJsZUN0cmwuZGF0YUNlbnRlcnNEYXRhID0gcmVzcG9uc2UucmVzdWx0O1xuICAgICAgICAgICAgZGF0YUNlbnRlcnNUYWJsZUN0cmwuZHRJbnN0YW5jZSA9IHt9O1xuICAgICAgICAgICAgZGF0YUNlbnRlcnNUYWJsZUN0cmwuZHRDb2x1bW5zID0gW107XG4gICAgICAgICAgICBkYXRhQ2VudGVyc1RhYmxlQ3RybC5kdENvbHVtbkRlZnMgPSBbXTtcbiAgICAgICAgICAgIGRhdGFDZW50ZXJzVGFibGVDdHJsLmhlYWRlcnMgPSBbXG4gICAgICAgICAgICAgICAge1xuICAgICAgICAgICAgICAgICAgICBjb2x1bW4gOiAnZGMnLFxuICAgICAgICAgICAgICAgICAgICBuYW1lIDogJ05hbWUnLFxuICAgICAgICAgICAgICAgICAgICBjbGlja0FibGUgOiB0cnVlXG4gICAgICAgICAgICAgICAgfSxcbiAgICAgICAgICAgICAgICB7XG4gICAgICAgICAgICAgICAgICAgIGNvbHVtbiA6ICdlZmZlY3RpdmVfaXAnLFxuICAgICAgICAgICAgICAgICAgICBuYW1lIDogJ0lQIEFkZHJlc3MnLFxuICAgICAgICAgICAgICAgICAgICBjbGlja0FibGUgOiB0cnVlXG4gICAgICAgICAgICAgICAgfSxcbiAgICAgICAgICAgICAgICB7XG4gICAgICAgICAgICAgICAgICAgIGNvbHVtbiA6ICdub2RlX2lkJyxcbiAgICAgICAgICAgICAgICAgICAgbmFtZSA6ICdOb2RlIElkJyxcbiAgICAgICAgICAgICAgICAgICAgY2xpY2tBYmxlIDogdHJ1ZVxuICAgICAgICAgICAgICAgIH0sXG4gICAgICAgICAgICAgICAge1xuICAgICAgICAgICAgICAgICAgICBjb2x1bW4gOiAnbm90ZXMnLFxuICAgICAgICAgICAgICAgICAgICBuYW1lIDogJ0Rlc2NyaXB0aW9uJyxcbiAgICAgICAgICAgICAgICAgICAgY2xpY2tBYmxlIDogZmFsc2VcbiAgICAgICAgICAgICAgICB9LFxuICAgICAgICAgICAgICAgIHtcbiAgICAgICAgICAgICAgICAgICAgY29sdW1uIDogJ3N0YXR1cycsXG4gICAgICAgICAgICAgICAgICAgIG5hbWUgOiAnU3RhdHVzJyxcbiAgICAgICAgICAgICAgICAgICAgY2xpY2tBYmxlIDogZmFsc2VcbiAgICAgICAgICAgICAgICB9XG4gICAgICAgICAgICBdO1xuXG4gICAgICAgICAgICB2YXIgY2xpY2tBYmxlQ29sdW1uID0gZnVuY3Rpb24gKGRhdGEsIHR5cGUsIGZ1bGwsIG1ldGEpIHtcbiAgICAgICAgICAgICAgICByZXR1cm4gJzxhIG5nLWNsaWNrPVwiZGF0YUNlbnRlcnNUYWJsZUN0cmwub3BlbkRhdGFDZW50ZXIoXFwnJyArIGZ1bGwuZGF0YV9jZW50ZXJfaWQgKyAnXFwnKVwiPicgKyBkYXRhICsgJzwvYT4nO1xuICAgICAgICAgICAgfTtcblxuICAgICAgICAgICAgdmFyIGNoYW5nZVRvTG9jYWxEYXRlID0gZnVuY3Rpb24oZGF0YSwgdHlwZSwgZnVsbCwgbWV0YSl7XG4gICAgICAgICAgICAgICAgcmV0dXJuIG1vbWVudChkYXRhKS5mb3JtYXQoJ0REIE1NTSBZWVlZLCBISDptbScpXG4gICAgICAgICAgICB9O1xuXG5cbiAgICAgICAgICAgIGZvciAodmFyIGkgPSAwOyBpIDwgIGRhdGFDZW50ZXJzVGFibGVDdHJsLmhlYWRlcnMubGVuZ3RoIDsgaSsrKSB7XG4gICAgICAgICAgICAgICAgaWYgKGRhdGFDZW50ZXJzVGFibGVDdHJsLmhlYWRlcnNbaV0udHlwZSA9PSAnZGF0ZScpe1xuICAgICAgICAgICAgICAgICAgICBkYXRhQ2VudGVyc1RhYmxlQ3RybC5kdENvbHVtbnMucHVzaChEVENvbHVtbkJ1aWxkZXIubmV3Q29sdW1uKGRhdGFDZW50ZXJzVGFibGVDdHJsLmhlYWRlcnNbaV0uY29sdW1uKS53aXRoVGl0bGUoZGF0YUNlbnRlcnNUYWJsZUN0cmwuaGVhZGVyc1tpXS5uYW1lKS5yZW5kZXJXaXRoKGNoYW5nZVRvTG9jYWxEYXRlKSk7XG4gICAgICAgICAgICAgICAgfVxuICAgICAgICAgICAgICAgIGVsc2V7XG4gICAgICAgICAgICAgICAgICAgIGRhdGFDZW50ZXJzVGFibGVDdHJsLmR0Q29sdW1ucy5wdXNoKERUQ29sdW1uQnVpbGRlci5uZXdDb2x1bW4oZGF0YUNlbnRlcnNUYWJsZUN0cmwuaGVhZGVyc1tpXS5jb2x1bW4pLndpdGhUaXRsZShkYXRhQ2VudGVyc1RhYmxlQ3RybC5oZWFkZXJzW2ldLm5hbWUpKTtcbiAgICAgICAgICAgICAgICB9XG4gICAgICAgICAgICB9XG5cbiAgICAgICAgICAgIHZhciBnZXRUYWJsZURhdGEgPSBmdW5jdGlvbiAoKSB7XG4gICAgICAgICAgICAgICAgdmFyIGRlZmVycmVkID0gJHEuZGVmZXIoKTtcbiAgICAgICAgICAgICAgICBkZWZlcnJlZC5yZXNvbHZlKGRhdGFDZW50ZXJzVGFibGVDdHJsLmRhdGFDZW50ZXJzRGF0YSk7XG4gICAgICAgICAgICAgICAgcmV0dXJuIGRlZmVycmVkLnByb21pc2U7XG4gICAgICAgICAgICB9O1xuXG4gICAgICAgICAgICBkYXRhQ2VudGVyc1RhYmxlQ3RybC5kdE9wdGlvbnMgPSBEVE9wdGlvbnNCdWlsZGVyLmZyb21GblByb21pc2UoZnVuY3Rpb24gKCkge1xuICAgICAgICAgICAgICAgICAgICByZXR1cm4gZ2V0VGFibGVEYXRhKCk7XG4gICAgICAgICAgICAgICAgfSlcbiAgICAgICAgICAgICAgICAud2l0aERPTSgnPFwiaHRtbDVidXR0b25zXCJCPmxUZmdpdHAnKVxuICAgICAgICAgICAgICAgIC53aXRoT3B0aW9uKCdjcmVhdGVkUm93JywgZnVuY3Rpb24gKHJvdykge1xuICAgICAgICAgICAgICAgICAgICAvLyBSZWNvbXBpbGluZyBzbyB3ZSBjYW4gYmluZCBBbmd1bGFyIGRpcmVjdGl2ZSB0byB0aGUgRFRcbiAgICAgICAgICAgICAgICAgICAgJGNvbXBpbGUoYW5ndWxhci5lbGVtZW50KHJvdykuY29udGVudHMoKSkoJHNjb3BlKTtcbiAgICAgICAgICAgICAgICB9KVxuICAgICAgICAgICAgICAgIC53aXRoT3B0aW9uKCdzY3JvbGxYJywgZmFsc2UpXG4gICAgICAgICAgICAgICAgLndpdGhCdXR0b25zKFtcbiAgICAgICAgICAgICAgICBdKVxuICAgICAgICAgICAgICAgIC53aXRoT3B0aW9uKCdzZWFyY2gnLHtcbiAgICAgICAgICAgICAgICAgICAgXCJjYXNlSW5zZW5zaXRpdmVcIjogdHJ1ZSxcbiAgICAgICAgICAgICAgICAgICAgXCJ1c2VXaWxkY2FyZHNcIiA6IGZhbHNlXG4gICAgICAgICAgICAgICAgfSk7XG4gICAgICAgICAgICAgICAgXG4gICAgICAgICAgICAgICAgaWYgKGRhdGFDZW50ZXJzVGFibGVDdHJsLmRhdGFDZW50ZXJzRGF0YSAmJiBkYXRhQ2VudGVyc1RhYmxlQ3RybC5kYXRhQ2VudGVyc0RhdGEubGVuZ3RoID4gMCl7XG4gICAgICAgICAgICAgICAgICAgIGRhdGFDZW50ZXJzVGFibGVDdHJsLmR0T3B0aW9ucy53aXRoTGlnaHRDb2x1bW5GaWx0ZXIoe1xuICAgICAgICAgICAgICAgICAgICAgICAgICAgIDAgOiB7XG4gICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgIHR5cGU6ICd0ZXh0J1xuICAgICAgICAgICAgICAgICAgICAgICAgICAgIH0sXG4gICAgICAgICAgICAgICAgICAgICAgICAgICAgMSA6IHtcbiAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgdHlwZTogJ3RleHQnXG4gICAgICAgICAgICAgICAgICAgICAgICAgICAgfSxcbiAgICAgICAgICAgICAgICAgICAgICAgICAgICAyIDoge1xuICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICB0eXBlOiAndGV4dCdcbiAgICAgICAgICAgICAgICAgICAgICAgICAgICB9LFxuICAgICAgICAgICAgICAgICAgICAgICAgICAgIDMgOiB7XG4gICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgIHR5cGU6ICd0ZXh0J1xuICAgICAgICAgICAgICAgICAgICAgICAgICAgIH0sXG4gICAgICAgICAgICAgICAgICAgICAgICAgICAgNCA6IHtcbiAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgdHlwZTogJ3RleHQnXG4gICAgICAgICAgICAgICAgICAgICAgICAgICAgfVxuICAgICAgICAgICAgICAgICAgICB9KTtcbiAgICAgICAgICAgICAgICB9XG5cbiAgICAgICAgICAgIGRhdGFDZW50ZXJzVGFibGVDdHJsLmR0SW5zdGFuY2VDYWxsYmFjayA9IGZ1bmN0aW9uIChkdEluc3RhbmNlKSB7XG4gICAgICAgICAgICAgICAgaWYgKGFuZ3VsYXIuaXNGdW5jdGlvbihkYXRhQ2VudGVyc1RhYmxlQ3RybC5kdEluc3RhbmNlKSkge1xuICAgICAgICAgICAgICAgICAgICBkYXRhQ2VudGVyc1RhYmxlQ3RybC5kdEluc3RhbmNlKGR0SW5zdGFuY2UpO1xuICAgICAgICAgICAgICAgIH0gZWxzZSBpZiAoYW5ndWxhci5pc0RlZmluZWQoZGF0YUNlbnRlcnNUYWJsZUN0cmwuZHRJbnN0YW5jZSkpIHtcbiAgICAgICAgICAgICAgICAgICAgZGF0YUNlbnRlcnNUYWJsZUN0cmwuZHRJbnN0YW5jZSA9IGR0SW5zdGFuY2U7XG4gICAgICAgICAgICAgICAgfVxuICAgICAgICAgICAgfTtcbiAgICAgICAgICAgIGlmIChkYXRhQ2VudGVyc1RhYmxlQ3RybC5kdEluc3RhbmNlLmNoYW5nZURhdGEgIT0gbnVsbClcbiAgICAgICAgICAgICAgICBkYXRhQ2VudGVyc1RhYmxlQ3RybC5kdEluc3RhbmNlLmNoYW5nZURhdGEoZ2V0VGFibGVEYXRhKCkpO1xuXG4gICAgICAgICAgICAgICAgJHRpbWVvdXQoKCkgPT4ge1xuICAgICAgICAgICAgICAgICAgICBkYXRhQ2VudGVyc1RhYmxlQ3RybC5sb2FkaW5nVGFibGUgPSBmYWxzZTtcbiAgICAgICAgICAgICAgICAgIH0pO1xuICAgICAgICAgIFxuICAgICAgICB9KTtcblxuICAgICAgICBkYXRhQ2VudGVyc1RhYmxlQ3RybC5vcGVuRGF0YUNlbnRlciA9IGZ1bmN0aW9uKGRhdGFfY2VudGVyX2lkKXtcbiAgICAgICAgICAgIGlmICgkc2NvcGUuY29udGVudC5vcGVuRGF0YUNlbnRlcikge1xuICAgICAgICAgICAgICAgIHZhciBkYXRhQ2VudGVyRGF0YSA9IF8uZmluZChkYXRhQ2VudGVyc1RhYmxlQ3RybC5kYXRhQ2VudGVyc0RhdGEsIHtkYXRhX2NlbnRlcl9pZDogZGF0YV9jZW50ZXJfaWR9KTtcbiAgICAgICAgICAgICAgICBpZiAoZGF0YUNlbnRlckRhdGEpIHtcbiAgICAgICAgICAgICAgICAgICAgJHNjb3BlLmNvbnRlbnQub3BlbkRhdGFDZW50ZXIoZGF0YUNlbnRlckRhdGEpO1xuICAgICAgICAgICAgICAgICAgICByZXR1cm47XG4gICAgICAgICAgICAgICAgfVxuICAgICAgICAgICAgfVxuICAgICAgICAgICAgLy9UT0RPIHNob3cgZXJyb3IgPz9cbiAgICAgICAgfTtcblxuICAgICAgICBkYXRhQ2VudGVyc1RhYmxlQ3RybC5vcGVuTmV3RGF0YUNlbnRlciA9IGZ1bmN0aW9uKCl7XG4gICAgICAgICAgICBpZiAoJHNjb3BlLmNvbnRlbnQub3Blbk5ld0RhdGFDZW50ZXIpIHtcbiAgICAgICAgICAgICAgICAkc2NvcGUuY29udGVudC5vcGVuTmV3RGF0YUNlbnRlcihkYXRhQ2VudGVyc1RhYmxlQ3RybC5kYXRhQ2VudGVyc0RhdGEpO1xuICAgICAgICAgICAgICAgIHJldHVybjtcbiAgICAgICAgICAgIH1cbiAgICAgICAgICAgIC8vVE9ETyBzaG93IGVycm9yID8/XG4gICAgICAgIH1cbiAgICB9O1xuXG4gICAgcmV0dXJuIHtcbiAgICAgICAgcmVzdHJpY3Q6IFwiRVwiLFxuICAgICAgICB0ZW1wbGF0ZVVybDogdGVtcGxhdGUsXG4gICAgICAgIHNjb3BlOiB7XG4gICAgICAgICAgICBjb250ZW50OiAnPSdcbiAgICAgICAgfSxcbiAgICAgICAgY29udHJvbGxlcjogY29udHJvbGxlcixcbiAgICAgICAgY29udHJvbGxlckFzIDonZGF0YUNlbnRlcnNUYWJsZUN0cmwnXG4gICAgfTtcbn1cblxuXG5hbmd1bGFyXG4gICAgLm1vZHVsZSgnVERNLUZFJylcbiAgICAuZGlyZWN0aXZlKCdjZW50ZXJzRGF0YVRhYmxlRGlyZWN0aXZlJywgY2VudGVyc0RhdGFUYWJsZURpcmVjdGl2ZSk7Il19