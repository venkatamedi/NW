function environmentsTableDirective(){return{restrict:"E",templateUrl:"views/environments/environmentsTable.html",scope:{content:"="},controller:function($scope,$compile,TDMService,DTColumnBuilder,DTOptionsBuilder,$q,$timeout){var environmentsTableCtrl=this;environmentsTableCtrl.loadingTable=!0,TDMService.getEnvironments().then((function(response){if("SUCCESS"==response.errorCode){environmentsTableCtrl.environmentsData=_.sortBy(response.result,(function(value){return new Date(value.environment_last_updated_date)})),environmentsTableCtrl.environmentsData.reverse(),environmentsTableCtrl.dtInstance={},environmentsTableCtrl.dtColumns=[],environmentsTableCtrl.dtColumnDefs=[],environmentsTableCtrl.headers=[{column:"environment_name",name:"Name",clickAble:!0},{column:"environment_type",name:"Environment Type",clickAble:!0},{column:"environment_point_of_contact_first_name",name:"Contact First Name",clickAble:!1},{column:"environment_point_of_contact_last_name",name:"Contact Last Name",clickAble:!1},{column:"environment_point_of_contact_email",name:"Contact Email",clickAble:!1},{column:"owners",name:"Owners",clickAble:!1},{column:"environment_creation_date",name:"Creation Date",clickAble:!1,type:"date"},{column:"environment_created_by",name:"Created By",clickAble:!1},{column:"environment_last_updated_date",name:"Last Update Date",clickAble:!1,type:"date"},{column:"environment_last_updated_by",name:"Updated By",clickAble:!1},{column:"environment_status",name:"Status",clickAble:!1}];for(var clickAbleColumn=function(data,type,full,meta){return'<a ng-click="environmentsTableCtrl.openEnvironment('+full.environment_id+')">'+data+"</a>"},changeToLocalDate=function(data,type,full,meta){return data?moment(data).format("DD MMM YYYY, HH:mm"):""},i=0;i<environmentsTableCtrl.headers.length;i++)1==environmentsTableCtrl.headers[i].clickAble?environmentsTableCtrl.dtColumns.push(DTColumnBuilder.newColumn(environmentsTableCtrl.headers[i].column).withTitle(environmentsTableCtrl.headers[i].name).renderWith(clickAbleColumn)):"date"==environmentsTableCtrl.headers[i].type?environmentsTableCtrl.dtColumns.push(DTColumnBuilder.newColumn(environmentsTableCtrl.headers[i].column).withTitle(environmentsTableCtrl.headers[i].name).renderWith(changeToLocalDate)):environmentsTableCtrl.dtColumns.push(DTColumnBuilder.newColumn(environmentsTableCtrl.headers[i].column).withTitle(environmentsTableCtrl.headers[i].name));var getTableData=function(){var deferred=$q.defer();return deferred.resolve(environmentsTableCtrl.environmentsData),deferred.promise};environmentsTableCtrl.dtOptions=DTOptionsBuilder.fromFnPromise((function(){return getTableData()})).withOption("aaSorting",[10,"asc"]).withDOM('<"html5buttons"B>lTfgitp').withOption("createdRow",(function(row){$compile(angular.element(row).contents())($scope)})).withOption("scrollX",!1).withButtons([]).withOption("caseInsensitive",!0).withOption("search",{caseInsensitive:!1}),environmentsTableCtrl.environmentsData&&environmentsTableCtrl.environmentsData.length>0&&environmentsTableCtrl.dtOptions.withLightColumnFilter({0:{type:"text"},1:{type:"select",values:[{value:"Source",label:"Source"},{value:"Target",label:"Target"},{value:"Both",label:"Both"}]},2:{type:"text"},3:{type:"text"},4:{type:"text"},5:{type:"text"},6:{type:"text"},7:{type:"select",values:_.map(_.unique(_.map(environmentsTableCtrl.environmentsData,"environment_created_by")),(function(el){return{value:el,label:el}}))},8:{type:"text"},9:{type:"select",values:_.map(_.unique(_.map(environmentsTableCtrl.environmentsData,"environment_last_updated_by")),(function(el){return{value:el,label:el}}))},10:{type:"select",defaultValue:"Active",values:[{value:"Inactive",label:"Inactive"},{value:"Active",label:"Active"}]}}),environmentsTableCtrl.dtInstanceCallback=function(dtInstance){angular.isFunction(environmentsTableCtrl.dtInstance)?environmentsTableCtrl.dtInstance(dtInstance):angular.isDefined(environmentsTableCtrl.dtInstance)&&(environmentsTableCtrl.dtInstance=dtInstance)},null!=environmentsTableCtrl.dtInstance.changeData&&environmentsTableCtrl.dtInstance.changeData(getTableData()),$timeout(()=>{environmentsTableCtrl.loadingTable=!1})}})),environmentsTableCtrl.openEnvironment=function(environmentID){if($scope.content.openEnvironment){var environmentData=_.find(environmentsTableCtrl.environmentsData,{environment_id:environmentID});if(environmentData)return void $scope.content.openEnvironment(environmentData,environmentsTableCtrl.environmentsData)}},environmentsTableCtrl.openNewEnvironment=function(){$scope.content.openNewEnvironment&&$scope.content.openNewEnvironment(environmentsTableCtrl.environmentsData)}},controllerAs:"environmentsTableCtrl"}}angular.module("TDM-FE").directive("environmentsTableDirective",environmentsTableDirective);
//# sourceMappingURL=data:application/json;charset=utf8;base64,eyJ2ZXJzaW9uIjozLCJzb3VyY2VzIjpbImRpcmVjdGl2ZXMvZW52aXJvbm1lbnRzL2Vudmlyb25tZW50c1RhYmxlRGlyZWN0aXZlLmpzIl0sIm5hbWVzIjpbImVudmlyb25tZW50c1RhYmxlRGlyZWN0aXZlIiwicmVzdHJpY3QiLCJ0ZW1wbGF0ZVVybCIsInNjb3BlIiwiY29udGVudCIsImNvbnRyb2xsZXIiLCIkc2NvcGUiLCIkY29tcGlsZSIsIlRETVNlcnZpY2UiLCJEVENvbHVtbkJ1aWxkZXIiLCJEVE9wdGlvbnNCdWlsZGVyIiwiJHEiLCIkdGltZW91dCIsImVudmlyb25tZW50c1RhYmxlQ3RybCIsInRoaXMiLCJsb2FkaW5nVGFibGUiLCJnZXRFbnZpcm9ubWVudHMiLCJ0aGVuIiwicmVzcG9uc2UiLCJlcnJvckNvZGUiLCJlbnZpcm9ubWVudHNEYXRhIiwiXyIsInNvcnRCeSIsInJlc3VsdCIsInZhbHVlIiwiRGF0ZSIsImVudmlyb25tZW50X2xhc3RfdXBkYXRlZF9kYXRlIiwicmV2ZXJzZSIsImR0SW5zdGFuY2UiLCJkdENvbHVtbnMiLCJkdENvbHVtbkRlZnMiLCJoZWFkZXJzIiwiY29sdW1uIiwibmFtZSIsImNsaWNrQWJsZSIsInR5cGUiLCJjbGlja0FibGVDb2x1bW4iLCJkYXRhIiwiZnVsbCIsIm1ldGEiLCJlbnZpcm9ubWVudF9pZCIsImNoYW5nZVRvTG9jYWxEYXRlIiwibW9tZW50IiwiZm9ybWF0IiwiaSIsImxlbmd0aCIsInB1c2giLCJuZXdDb2x1bW4iLCJ3aXRoVGl0bGUiLCJyZW5kZXJXaXRoIiwiZ2V0VGFibGVEYXRhIiwiZGVmZXJyZWQiLCJkZWZlciIsInJlc29sdmUiLCJwcm9taXNlIiwiZHRPcHRpb25zIiwiZnJvbUZuUHJvbWlzZSIsIndpdGhPcHRpb24iLCJ3aXRoRE9NIiwicm93IiwiYW5ndWxhciIsImVsZW1lbnQiLCJjb250ZW50cyIsIndpdGhCdXR0b25zIiwiY2FzZUluc2Vuc2l0aXZlIiwid2l0aExpZ2h0Q29sdW1uRmlsdGVyIiwiMCIsIjEiLCJ2YWx1ZXMiLCJsYWJlbCIsIjIiLCIzIiwiNCIsIjUiLCI2IiwiNyIsIm1hcCIsInVuaXF1ZSIsImVsIiwiOCIsIjkiLCIxMCIsImRlZmF1bHRWYWx1ZSIsImR0SW5zdGFuY2VDYWxsYmFjayIsImlzRnVuY3Rpb24iLCJpc0RlZmluZWQiLCJjaGFuZ2VEYXRhIiwib3BlbkVudmlyb25tZW50IiwiZW52aXJvbm1lbnRJRCIsImVudmlyb25tZW50RGF0YSIsImZpbmQiLCJvcGVuTmV3RW52aXJvbm1lbnQiLCJjb250cm9sbGVyQXMiLCJtb2R1bGUiLCJkaXJlY3RpdmUiXSwibWFwcGluZ3MiOiJBQUFBLFNBQVNBLDZCQXlPTCxNQUFPLENBQ0hDLFNBQVUsSUFDVkMsWUF6T1csNENBME9YQyxNQUFPLENBQ0hDLFFBQVMsS0FFYkMsV0EzT2EsU0FBVUMsT0FBT0MsU0FBU0MsV0FBV0MsZ0JBQWdCQyxpQkFBaUJDLEdBQUdDLFVBQ3RGLElBQUlDLHNCQUF3QkMsS0FFNUJELHNCQUFzQkUsY0FBZSxFQUVyQ1AsV0FBV1Esa0JBQWtCQyxNQUFLLFNBQVNDLFVBQ3ZDLEdBQTBCLFdBQXRCQSxTQUFTQyxVQUFiLENBS0FOLHNCQUFzQk8saUJBQWtCQyxFQUFFQyxPQUFPSixTQUFTSyxRQUFRLFNBQVNDLE9BQ3ZFLE9BQU8sSUFBSUMsS0FBS0QsTUFBTUUsa0NBRTFCYixzQkFBc0JPLGlCQUFpQk8sVUFDdkNkLHNCQUFzQmUsV0FBYSxHQUNuQ2Ysc0JBQXNCZ0IsVUFBWSxHQUNsQ2hCLHNCQUFzQmlCLGFBQWUsR0FDckNqQixzQkFBc0JrQixRQUFVLENBQzVCLENBQ0lDLE9BQVMsbUJBQ1RDLEtBQU8sT0FDUEMsV0FBWSxHQUVoQixDQUNJRixPQUFTLG1CQUNUQyxLQUFPLG1CQUNQQyxXQUFZLEdBRWhCLENBQ0lGLE9BQVMsMENBQ1RDLEtBQU8scUJBQ1BDLFdBQVksR0FFaEIsQ0FDSUYsT0FBUyx5Q0FDVEMsS0FBTyxvQkFDUEMsV0FBWSxHQUVoQixDQUNJRixPQUFTLHFDQUNUQyxLQUFPLGdCQUNQQyxXQUFZLEdBRWhC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c0JBQXNCTyxpQkFBa0IsaUNBQWdDLFNBQVMwRCxJQUMxRyxNQUFPLENBQUN0RCxNQUFRc0QsR0FBR1QsTUFBT1MsUUFHbENHLEdBQUssQ0FDRDlDLEtBQU0sU0FDTitDLGFBQWMsU0FDZGQsT0FBUSxDQUNKLENBQ0k1QyxNQUFRLFdBQ1I2QyxNQUFRLFlBRVosQ0FDSTdDLE1BQVEsU0FDUjZDLE1BQVEsY0FPcEN4RCxzQkFBc0JzRSxtQkFBcUIsU0FBVXZELFlBQzdDZ0MsUUFBUXdCLFdBQVd2RSxzQkFBc0JlLFlBQ3pDZixzQkFBc0JlLFdBQVdBLFlBQzFCZ0MsUUFBUXlCLFVBQVV4RSxzQkFBc0JlLGNBQy9DZixzQkFBc0JlLFdBQWFBLGFBR1EsTUFBL0NmLHNCQUFzQmUsV0FBVzBELFlBQ2pDekUsc0JBQXNCZSxXQUFXMEQsV0FBV3BDLGdCQUU1Q3RDLFNBQVMsS0FDTEMsc0JBQXNCRSxjQUFlLFFBS2pERixzQkFBc0IwRSxnQkFBa0IsU0FBU0MsZUFDN0MsR0FBSWxGLE9BQU9GLFFBQVFtRixnQkFBaUIsQ0FDaEMsSUFBSUUsZ0JBQWtCcEUsRUFBRXFFLEtBQUs3RSxzQkFBc0JPLGlCQUFrQixDQUFDb0IsZUFBZ0JnRCxnQkFDdEYsR0FBSUMsZ0JBRUEsWUFEQW5GLE9BQU9GLFFBQVFtRixnQkFBZ0JFLGdCQUFnQjVFLHNCQUFzQk8sb0JBT2pGUCxzQkFBc0I4RSxtQkFBcUIsV0FDbkNyRixPQUFPRixRQUFRdUYsb0JBQ1hyRixPQUFPRixRQUFRdUYsbUJBQW1COUUsc0JBQXNCTyxvQkFjcEV3RSxhQUFjLHlCQUt0QmhDLFFBQ0tpQyxPQUFPLFVBQ1BDLFVBQVUsNkJBQThCOUYiLCJmaWxlIjoiZGlyZWN0aXZlcy9lbnZpcm9ubWVudHMvZW52aXJvbm1lbnRzVGFibGVEaXJlY3RpdmUuanMiLCJzb3VyY2VzQ29udGVudCI6WyJmdW5jdGlvbiBlbnZpcm9ubWVudHNUYWJsZURpcmVjdGl2ZSgpe1xuXG4gICAgdmFyIHRlbXBsYXRlID0gXCJ2aWV3cy9lbnZpcm9ubWVudHMvZW52aXJvbm1lbnRzVGFibGUuaHRtbFwiO1xuXG4gICAgdmFyIGNvbnRyb2xsZXIgPSBmdW5jdGlvbiAoJHNjb3BlLCRjb21waWxlLFRETVNlcnZpY2UsRFRDb2x1bW5CdWlsZGVyLERUT3B0aW9uc0J1aWxkZXIsJHEsJHRpbWVvdXQpIHtcbiAgICAgICAgdmFyIGVudmlyb25tZW50c1RhYmxlQ3RybCA9IHRoaXM7XG5cbiAgICAgICAgZW52aXJvbm1lbnRzVGFibGVDdHJsLmxvYWRpbmdUYWJsZSA9IHRydWU7XG5cbiAgICAgICAgVERNU2VydmljZS5nZXRFbnZpcm9ubWVudHMoKS50aGVuKGZ1bmN0aW9uKHJlc3BvbnNlKXtcbiAgICAgICAgICAgIGlmIChyZXNwb25zZS5lcnJvckNvZGUgIT0gJ1NVQ0NFU1MnKXtcbiAgICAgICAgICAgICAgICAvL1RPRE8gc2hvdyBFcnJvclxuICAgICAgICAgICAgICAgIHJldHVybjtcbiAgICAgICAgICAgIH1cblxuICAgICAgICAgICAgZW52aXJvbm1lbnRzVGFibGVDdHJsLmVudmlyb25tZW50c0RhdGEgPV8uc29ydEJ5KHJlc3BvbnNlLnJlc3VsdCwgZnVuY3Rpb24odmFsdWUpIHtcbiAgICAgICAgICAgICAgICByZXR1cm4gbmV3IERhdGUodmFsdWUuZW52aXJvbm1lbnRfbGFzdF91cGRhdGVkX2RhdGUpO1xuICAgICAgICAgICAgfSk7XG4gICAgICAgICAgICBlbnZpcm9ubWVudHNUYWJsZUN0cmwuZW52aXJvbm1lbnRzRGF0YS5yZXZlcnNlKCk7XG4gICAgICAgICAgICBlbnZpcm9ubWVudHNUYWJsZUN0cmwuZHRJbnN0YW5jZSA9IHt9O1xuICAgICAgICAgICAgZW52aXJvbm1lbnRzVGFibGVDdHJsLmR0Q29sdW1ucyA9IFtdO1xuICAgICAgICAgICAgZW52aXJvbm1lbnRzVGFibGVDdHJsLmR0Q29sdW1uRGVmcyA9IFtdO1xuICAgICAgICAgICAgZW52aXJvbm1lbnRzVGFibGVDdHJsLmhlYWRlcnMgPSBbXG4gICAgICAgICAgICAgICAge1xuICAgICAgICAgICAgICAgICAgICBjb2x1bW4gOiAnZW52aXJvbm1lbnRfbmFtZScsXG4gICAgICAgICAgICAgICAgICAgIG5hbWUgOiAnTmFtZScsXG4gICAgICAgICAgICAgICAgICAgIGNsaWNrQWJsZSA6IHRydWVcbiAgICAgICAgICAgICAgICB9LFxuICAgICAgICAgICAgICAgIHtcbiAgICAgICAgICAgICAgICAgICAgY29sdW1uIDogJ2Vudmlyb25tZW50X3R5cGUnLFxuICAgICAgICAgICAgICAgICAgICBuYW1lIDogJ0Vudmlyb25tZW50IFR5cGUnLFxuICAgICAgICAgICAgICAgICAgICBjbGlja0FibGUgOiB0cnVlXG4gICAgICAgICAgICAgICAgfSxcbiAgICAgICAgICAgICAgICB7XG4gICAgICAgICAgICAgICAgICAgIGNvbHVtbiA6ICdlbnZpcm9ubWVudF9wb2ludF9vZl9jb250YWN0X2ZpcnN0X25hbWUnLFxuICAgICAgICAgICAgICAgICAgICBuYW1lIDogJ0NvbnRhY3QgRmlyc3QgTmFtZScsXG4gICAgICAgICAgICAgICAgICAgIGNsaWNrQWJsZSA6IGZhbHNlXG4gICAgICAgICAgICAgICAgfSxcbiAgICAgICAgICAgICAgICB7XG4gICAgICAgICAgICAgICAgICAgIGNvbHVtbiA6ICdlbnZpcm9ubWVudF9wb2ludF9vZl9jb250YWN0X2xhc3RfbmFtZScsXG4gICAgICAgICAgICAgICAgICAgIG5hbWUgOiAnQ29udGFjdCBMYXN0IE5hbWUnLFxuICAgICAgICAgICAgICAgICAgICBjbGlja0FibGUgOiBmYWxzZVxuICAgICAgICAgICAgICAgIH0sXG4gICAgICAgICAgICAgICAge1xuICAgICAgICAgICAgICAgICAgICBjb2x1bW4gOiAnZW52aXJvbm1lbnRfcG9pbnRfb2ZfY29udGFjdF9lbWFpbCcsXG4gICAgICAgICAgICAgICAgICAgIG5hbWUgOiAnQ29udGFjdCBFbWFpbCcsXG4gICAgICAgICAgICAgICAgICAgIGNsaWNrQWJsZSA6IGZhbHNlXG4gICAgICAgICAgICAgICAgfSxcbiAgICAgICAgICAgICAgICB7XG4gICAgICAgICAgICAgICAgICAgIGNvbHVtbiA6ICdvd25lcnMnLFxuICAgICAgICAgICAgICAgICAgICBuYW1lIDogJ093bmVycycsXG4gICAgICAgICAgICAgICAgICAgIGNsaWNrQWJsZSA6IGZhbHNlXG4gICAgICAgICAgICAgICAgfSxcbiAgICAgICAgICAgICAgICB7XG4gICAgICAgICAgICAgICAgICAgIGNvbHVtbiA6ICdlbnZpcm9ubWVudF9jcmVhdGlvbl9kYXRlJyxcbiAgICAgICAgICAgICAgICAgICAgbmFtZSA6ICdDcmVhdGlvbiBEYXRlJyxcbiAgICAgICAgICAgICAgICAgICAgY2xpY2tBYmxlIDogZmFsc2UsXG4gICAgICAgICAgICAgICAgICAgIHR5cGU6ICdkYXRlJ1xuICAgICAgICAgICAgICAgIH0sXG4gICAgICAgICAgICAgICAge1xuICAgICAgICAgICAgICAgICAgICBjb2x1bW4gOiAnZW52aXJvbm1lbnRfY3JlYXRlZF9ieScsXG4gICAgICAgICAgICAgICAgICAgIG5hbWUgOiAnQ3JlYXRlZCBCeScsXG4gICAgICAgICAgICAgICAgICAgIGNsaWNrQWJsZSA6IGZhbHNlXG4gICAgICAgICAgICAgICAgfSxcbiAgICAgICAgICAgICAgICB7XG4gICAgICAgICAgICAgICAgICAgIGNvbHVtbiA6ICdlbnZpcm9ubWVudF9sYXN0X3VwZGF0ZWRfZGF0ZScsXG4gICAgICAgICAgICAgICAgICAgIG5hbWUgOiAnTGFzdCBVcGRhdGUgRGF0ZScsXG4gICAgICAgICAgICAgICAgICAgIGNsaWNrQWJsZSA6IGZhbHNlLFxuICAgICAgICAgICAgICAgICAgICB0eXBlOiAnZGF0ZSdcbiAgICAgICAgICAgICAgICB9LFxuICAgICAgICAgICAgICAgIHtcbiAgICAgICAgICAgICAgICAgICAgY29sdW1uIDogJ2Vudmlyb25tZW50X2xhc3RfdXBkYXRlZF9ieScsXG4gICAgICAgICAgICAgICAgICAgIG5hbWUgOiAnVXBkYXRlZCBCeScsXG4gICAgICAgICAgICAgICAgICAgIGNsaWNrQWJsZSA6IGZhbHNlXG4gICAgICAgICAgICAgICAgfSxcbiAgICAgICAgICAgICAgICB7XG4gICAgICAgICAgICAgICAgICAgIGNvbHVtbiA6ICdlbnZpcm9ubWVudF9zdGF0dXMnLFxuICAgICAgICAgICAgICAgICAgICBuYW1lIDogJ1N0YXR1cycsXG4gICAgICAgICAgICAgICAgICAgIGNsaWNrQWJsZSA6IGZhbHNlXG4gICAgICAgICAgICAgICAgfVxuICAgICAgICAgICAgXTtcblxuICAgICAgICAgICAgdmFyIGNsaWNrQWJsZUNvbHVtbiA9IGZ1bmN0aW9uIChkYXRhLCB0eXBlLCBmdWxsLCBtZXRhKSB7XG4gICAgICAgICAgICAgICAgcmV0dXJuICc8YSBuZy1jbGljaz1cImVudmlyb25tZW50c1RhYmxlQ3RybC5vcGVuRW52aXJvbm1lbnQoJyArIGZ1bGwuZW52aXJvbm1lbnRfaWQgKyAnKVwiPicgKyBkYXRhICsgJzwvYT4nO1xuICAgICAgICAgICAgfTtcblxuICAgICAgICAgICAgdmFyIGNoYW5nZVRvTG9jYWxEYXRlID0gZnVuY3Rpb24oZGF0YSwgdHlwZSwgZnVsbCwgbWV0YSl7XG4gICAgICAgICAgICAgICAgaWYgKGRhdGEpXG4gICAgICAgICAgICAgICAgICAgIHJldHVybiBtb21lbnQoZGF0YSkuZm9ybWF0KCdERCBNTU0gWVlZWSwgSEg6bW0nKTtcbiAgICAgICAgICAgICAgICByZXR1cm4gJyc7XG4gICAgICAgICAgICB9O1xuXG4gICAgICAgICAgICBmb3IgKHZhciBpID0gMDsgaSA8ICBlbnZpcm9ubWVudHNUYWJsZUN0cmwuaGVhZGVycy5sZW5ndGggOyBpKyspIHtcbiAgICAgICAgICAgICAgICBpZiAoZW52aXJvbm1lbnRzVGFibGVDdHJsLmhlYWRlcnNbaV0uY2xpY2tBYmxlID09IHRydWUpIHtcbiAgICAgICAgICAgICAgICAgICAgZW52aXJvbm1lbnRzVGFibGVDdHJsLmR0Q29sdW1ucy5wdXNoKERUQ29sdW1uQnVpbGRlci5uZXdDb2x1bW4oZW52aXJvbm1lbnRzVGFibGVDdHJsLmhlYWRlcnNbaV0uY29sdW1uKS53aXRoVGl0bGUoZW52aXJvbm1lbnRzVGFibGVDdHJsLmhlYWRlcnNbaV0ubmFtZSkucmVuZGVyV2l0aChjbGlja0FibGVDb2x1bW4pKTtcbiAgICAgICAgICAgICAgICB9XG4gICAgICAgICAgICAgICAgZWxzZSBpZiAoZW52aXJvbm1lbnRzVGFibGVDdHJsLmhlYWRlcnNbaV0udHlwZSA9PSAnZGF0ZScpe1xuICAgICAgICAgICAgICAgICAgICBlbnZpcm9ubWVudHNUYWJsZUN0cmwuZHRDb2x1bW5zLnB1c2goRFRDb2x1bW5CdWlsZGVyLm5ld0NvbHVtbihlbnZpcm9ubWVudHNUYWJsZUN0cmwuaGVhZGVyc1tpXS5jb2x1bW4pLndpdGhUaXRsZShlbnZpcm9ubWVudHNUYWJsZUN0cmwuaGVhZGVyc1tpXS5uYW1lKS5yZW5kZXJXaXRoKGNoYW5nZVRvTG9jYWxEYXRlKSk7XG4gICAgICAgICAgICAgICAgfVxuICAgICAgICAgICAgICAgIGVsc2Uge1xuICAgICAgICAgICAgICAgICAgICBlbnZpcm9ubWVudHNUYWJsZUN0cmwuZHRDb2x1bW5zLnB1c2goRFRDb2x1bW5CdWlsZGVyLm5ld0NvbHVtbihlbnZpcm9ubWVudHNUYWJsZUN0cmwuaGVhZGVyc1tpXS5jb2x1bW4pLndpdGhUaXRsZShlbnZpcm9ubWVudHNUYWJsZUN0cmwuaGVhZGVyc1tpXS5uYW1lKSk7XG4gICAgICAgICAgICAgICAgfVxuICAgICAgICAgICAgfVxuXG4gICAgICAgICAgICB2YXIgZ2V0VGFibGVEYXRhID0gZnVuY3Rpb24gKCkge1xuICAgICAgICAgICAgICAgIHZhciBkZWZlcnJlZCA9ICRxLmRlZmVyKCk7XG4gICAgICAgICAgICAgICAgZGVmZXJyZWQucmVzb2x2ZShlbnZpcm9ubWVudHNUYWJsZUN0cmwuZW52aXJvbm1lbnRzRGF0YSk7XG4gICAgICAgICAgICAgICAgcmV0dXJuIGRlZmVycmVkLnByb21pc2U7XG4gICAgICAgICAgICB9O1xuXG4gICAgICAgICAgICBlbnZpcm9ubWVudHNUYWJsZUN0cmwuZHRPcHRpb25zID0gRFRPcHRpb25zQnVpbGRlci5mcm9tRm5Qcm9taXNlKGZ1bmN0aW9uICgpIHtcbiAgICAgICAgICAgICAgICAgICAgcmV0dXJuIGdldFRhYmxlRGF0YSgpO1xuICAgICAgICAgICAgICAgIH0pXG4gICAgICAgICAgICAgICAgLndpdGhPcHRpb24oJ2FhU29ydGluZycsIFsxMCwgJ2FzYyddKVxuICAgICAgICAgICAgICAgIC53aXRoRE9NKCc8XCJodG1sNWJ1dHRvbnNcIkI+bFRmZ2l0cCcpXG4gICAgICAgICAgICAgICAgLndpdGhPcHRpb24oJ2NyZWF0ZWRSb3cnLCBmdW5jdGlvbiAocm93KSB7XG4gICAgICAgICAgICAgICAgICAgIC8vIFJlY29tcGlsaW5nIHNvIHdlIGNhbiBiaW5kIEFuZ3VsYXIgZGlyZWN0aXZlIHRvIHRoZSBEVFxuICAgICAgICAgICAgICAgICAgICAkY29tcGlsZShhbmd1bGFyLmVsZW1lbnQocm93KS5jb250ZW50cygpKSgkc2NvcGUpO1xuICAgICAgICAgICAgICAgIH0pXG4gICAgICAgICAgICAgICAgLndpdGhPcHRpb24oJ3Njcm9sbFgnLCBmYWxzZSlcbiAgICAgICAgICAgICAgICAud2l0aEJ1dHRvbnMoW1xuICAgICAgICAgICAgICAgIF0pXG4gICAgICAgICAgICAgICAgLndpdGhPcHRpb24oXCJjYXNlSW5zZW5zaXRpdmVcIix0cnVlKVxuICAgICAgICAgICAgICAgIC53aXRoT3B0aW9uKCdzZWFyY2gnLHtcbiAgICAgICAgICAgICAgICAgICAgXCJjYXNlSW5zZW5zaXRpdmVcIjogZmFsc2VcbiAgICAgICAgICAgICAgICB9KTtcbiAgICAgICAgICAgICAgICBcbiAgICAgICAgICAgICAgICBpZiAoZW52aXJvbm1lbnRzVGFibGVDdHJsLmVudmlyb25tZW50c0RhdGEgJiYgZW52aXJvbm1lbnRzVGFibGVDdHJsLmVudmlyb25tZW50c0RhdGEubGVuZ3RoID4gMCl7XG4gICAgICAgICAgICAgICAgICAgIGVudmlyb25tZW50c1RhYmxlQ3RybC5kdE9wdGlvbnNcbiAgICAgICAgICAgICAgICAgICAgLndpdGhMaWdodENvbHVtbkZpbHRlcih7XG4gICAgICAgICAgICAgICAgICAgICAgICAgICAgMCA6IHtcbiAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgdHlwZTogJ3RleHQnXG4gICAgICAgICAgICAgICAgICAgICAgICAgICAgfSxcbiAgICAgICAgICAgICAgICAgICAgICAgICAgICAxIDoge1xuICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICB0eXBlOiAnc2VsZWN0JyxcbiAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgdmFsdWVzOiBbXG4gICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICB7XG4gICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgdmFsdWUgOiBcIlNvdXJjZVwiLFxuICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgIGxhYmVsIDogXCJTb3VyY2VcIlxuICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgfSxcbiAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgIHtcbiAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICB2YWx1ZSA6IFwiVGFyZ2V0XCIsXG4gICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgbGFiZWwgOiBcIlRhcmdldFwiXG4gICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICB9LFxuICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAge1xuICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgIHZhbHVlIDogXCJCb3RoXCIsXG4gICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgbGFiZWwgOiBcIkJvdGhcIlxuICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgfVxuICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICBdXG4gICAgICAgICAgICAgICAgICAgICAgICAgICAgfSxcbiAgICAgICAgICAgICAgICAgICAgICAgICAgICAyIDoge1xuICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICB0eXBlOiAndGV4dCdcbiAgICAgICAgICAgICAgICAgICAgICAgICAgICB9LFxuICAgICAgICAgICAgICAgICAgICAgICAgICAgIDMgOntcbiAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgdHlwZTogJ3RleHQnXG4gICAgICAgICAgICAgICAgICAgICAgICAgICAgfSxcbiAgICAgICAgICAgICAgICAgICAgICAgICAgICA0IDoge1xuICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICB0eXBlOiAndGV4dCdcbiAgICAgICAgICAgICAgICAgICAgICAgICAgICB9LFxuICAgICAgICAgICAgICAgICAgICAgICAgICAgIDUgOiB7XG4gICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgIHR5cGU6ICd0ZXh0J1xuICAgICAgICAgICAgICAgICAgICAgICAgICAgIH0sXG4gICAgICAgICAgICAgICAgICAgICAgICAgICAgNiA6IHtcbiAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgdHlwZTogJ3RleHQnXG4gICAgICAgICAgICAgICAgICAgICAgICAgICAgfSxcbiAgICAgICAgICAgICAgICAgICAgICAgICAgICA3IDoge1xuICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICB0eXBlOiAnc2VsZWN0JyxcbiAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgdmFsdWVzOiBfLm1hcChfLnVuaXF1ZShfLm1hcChlbnZpcm9ubWVudHNUYWJsZUN0cmwuZW52aXJvbm1lbnRzRGF0YSwgJ2Vudmlyb25tZW50X2NyZWF0ZWRfYnknKSksZnVuY3Rpb24oZWwpe1xuICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgcmV0dXJuIHt2YWx1ZSA6IGVsLGxhYmVsIDplbH1cbiAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgfSlcbiAgICAgICAgICAgICAgICAgICAgICAgICAgICB9LFxuICAgICAgICAgICAgICAgICAgICAgICAgICAgIDggOiB7XG4gICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgIHR5cGU6ICd0ZXh0J1xuICAgICAgICAgICAgICAgICAgICAgICAgICAgIH0sXG4gICAgICAgICAgICAgICAgICAgICAgICAgICAgOSA6IHtcbiAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgdHlwZTogJ3NlbGVjdCcsXG4gICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgIHZhbHVlczogXy5tYXAoXy51bmlxdWUoXy5tYXAoZW52aXJvbm1lbnRzVGFibGVDdHJsLmVudmlyb25tZW50c0RhdGEsICdlbnZpcm9ubWVudF9sYXN0X3VwZGF0ZWRfYnknKSksZnVuY3Rpb24oZWwpe1xuICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgcmV0dXJuIHt2YWx1ZSA6IGVsLGxhYmVsIDplbH1cbiAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgfSlcbiAgICAgICAgICAgICAgICAgICAgICAgICAgICB9LFxuICAgICAgICAgICAgICAgICAgICAgICAgICAgIDEwIDoge1xuICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICB0eXBlOiAnc2VsZWN0JyxcbiAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgZGVmYXVsdFZhbHVlOiAnQWN0aXZlJyxcbiAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgdmFsdWVzOiBbXG4gICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICB7XG4gICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgdmFsdWUgOiBcIkluYWN0aXZlXCIsXG4gICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgbGFiZWwgOiBcIkluYWN0aXZlXCJcbiAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgIH0sXG4gICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICB7XG4gICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgdmFsdWUgOiBcIkFjdGl2ZVwiLFxuICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgIGxhYmVsIDogXCJBY3RpdmVcIlxuICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgfVxuICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICBdXG4gICAgICAgICAgICAgICAgICAgICAgICAgICAgfVxuICAgICAgICAgICAgICAgICAgICB9KTtcbiAgICAgICAgICAgICAgICB9XG5cbiAgICAgICAgICAgIGVudmlyb25tZW50c1RhYmxlQ3RybC5kdEluc3RhbmNlQ2FsbGJhY2sgPSBmdW5jdGlvbiAoZHRJbnN0YW5jZSkge1xuICAgICAgICAgICAgICAgIGlmIChhbmd1bGFyLmlzRnVuY3Rpb24oZW52aXJvbm1lbnRzVGFibGVDdHJsLmR0SW5zdGFuY2UpKSB7XG4gICAgICAgICAgICAgICAgICAgIGVudmlyb25tZW50c1RhYmxlQ3RybC5kdEluc3RhbmNlKGR0SW5zdGFuY2UpO1xuICAgICAgICAgICAgICAgIH0gZWxzZSBpZiAoYW5ndWxhci5pc0RlZmluZWQoZW52aXJvbm1lbnRzVGFibGVDdHJsLmR0SW5zdGFuY2UpKSB7XG4gICAgICAgICAgICAgICAgICAgIGVudmlyb25tZW50c1RhYmxlQ3RybC5kdEluc3RhbmNlID0gZHRJbnN0YW5jZTtcbiAgICAgICAgICAgICAgICB9XG4gICAgICAgICAgICB9O1xuICAgICAgICAgICAgaWYgKGVudmlyb25tZW50c1RhYmxlQ3RybC5kdEluc3RhbmNlLmNoYW5nZURhdGEgIT0gbnVsbClcbiAgICAgICAgICAgICAgICBlbnZpcm9ubWVudHNUYWJsZUN0cmwuZHRJbnN0YW5jZS5jaGFuZ2VEYXRhKGdldFRhYmxlRGF0YSgpKTtcblxuICAgICAgICAgICAgICAgICR0aW1lb3V0KCgpID0+IHtcbiAgICAgICAgICAgICAgICAgICAgZW52aXJvbm1lbnRzVGFibGVDdHJsLmxvYWRpbmdUYWJsZSA9IGZhbHNlO1xuICAgICAgICAgICAgICAgIH0pO1xuICAgICAgICB9KTtcblxuXG4gICAgICAgIGVudmlyb25tZW50c1RhYmxlQ3RybC5vcGVuRW52aXJvbm1lbnQgPSBmdW5jdGlvbihlbnZpcm9ubWVudElEKXtcbiAgICAgICAgICAgIGlmICgkc2NvcGUuY29udGVudC5vcGVuRW52aXJvbm1lbnQpIHtcbiAgICAgICAgICAgICAgICB2YXIgZW52aXJvbm1lbnREYXRhID0gXy5maW5kKGVudmlyb25tZW50c1RhYmxlQ3RybC5lbnZpcm9ubWVudHNEYXRhLCB7ZW52aXJvbm1lbnRfaWQ6IGVudmlyb25tZW50SUR9KTtcbiAgICAgICAgICAgICAgICBpZiAoZW52aXJvbm1lbnREYXRhKSB7XG4gICAgICAgICAgICAgICAgICAgICRzY29wZS5jb250ZW50Lm9wZW5FbnZpcm9ubWVudChlbnZpcm9ubWVudERhdGEsZW52aXJvbm1lbnRzVGFibGVDdHJsLmVudmlyb25tZW50c0RhdGEpO1xuICAgICAgICAgICAgICAgICAgICByZXR1cm47XG4gICAgICAgICAgICAgICAgfVxuICAgICAgICAgICAgfVxuICAgICAgICAgICAgLy9UT0RPIHNob3cgZXJyb3IgPz9cbiAgICAgICAgfTtcblxuICAgICAgICBlbnZpcm9ubWVudHNUYWJsZUN0cmwub3Blbk5ld0Vudmlyb25tZW50ID0gZnVuY3Rpb24oKXtcbiAgICAgICAgICAgIGlmICgkc2NvcGUuY29udGVudC5vcGVuTmV3RW52aXJvbm1lbnQpIHtcbiAgICAgICAgICAgICAgICAgICAgJHNjb3BlLmNvbnRlbnQub3Blbk5ld0Vudmlyb25tZW50KGVudmlyb25tZW50c1RhYmxlQ3RybC5lbnZpcm9ubWVudHNEYXRhKTtcbiAgICAgICAgICAgICAgICByZXR1cm47XG4gICAgICAgICAgICB9XG4gICAgICAgICAgICAvL1RPRE8gc2hvdyBlcnJvciA/P1xuICAgICAgICB9XG4gICAgfTtcblxuICAgIHJldHVybiB7XG4gICAgICAgIHJlc3RyaWN0OiBcIkVcIixcbiAgICAgICAgdGVtcGxhdGVVcmw6IHRlbXBsYXRlLFxuICAgICAgICBzY29wZToge1xuICAgICAgICAgICAgY29udGVudDogJz0nXG4gICAgICAgIH0sXG4gICAgICAgIGNvbnRyb2xsZXI6IGNvbnRyb2xsZXIsXG4gICAgICAgIGNvbnRyb2xsZXJBcyA6J2Vudmlyb25tZW50c1RhYmxlQ3RybCdcbiAgICB9O1xufVxuXG5cbmFuZ3VsYXJcbiAgICAubW9kdWxlKCdURE0tRkUnKVxuICAgIC5kaXJlY3RpdmUoJ2Vudmlyb25tZW50c1RhYmxlRGlyZWN0aXZlJywgZW52aXJvbm1lbnRzVGFibGVEaXJlY3RpdmUpOyJdfQ==