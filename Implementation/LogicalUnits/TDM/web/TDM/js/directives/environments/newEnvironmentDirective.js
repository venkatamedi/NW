function newEnvironmentDirective(){return{restrict:"E",templateUrl:"views/environments/newEnvironment.html",scope:{content:"="},controller:function($scope,TDMService,BreadCrumbsService,toastr,$timeout,AuthService){var newEnvironmentCtrl=this;newEnvironmentCtrl.envTypes=["Both","Source","Target"],newEnvironmentCtrl.syncModes=[{text:"Do not Sync",value:"OFF"},{text:"Always Sync",value:"FORCE"}],newEnvironmentCtrl.environments=$scope.content.environments,TDMService.getFabricRoles("owner").then(response=>{newEnvironmentCtrl.userGroups=response.result}),newEnvironmentCtrl.envType="Target",newEnvironmentCtrl.environmentData={allow_write:!0,allow_read:!1,owners:[]},newEnvironmentCtrl.envTypeChanged=function(){newEnvironmentCtrl.envType&&("target"==newEnvironmentCtrl.envType.toLowerCase()?(newEnvironmentCtrl.environmentData.allow_write=!0,newEnvironmentCtrl.environmentData.allow_read=!1):"source"==newEnvironmentCtrl.envType.toLowerCase()?(newEnvironmentCtrl.environmentData.allow_write=!1,newEnvironmentCtrl.environmentData.allow_read=!0):"both"==newEnvironmentCtrl.envType.toLowerCase()&&(newEnvironmentCtrl.environmentData.allow_write=!0,newEnvironmentCtrl.environmentData.allow_read=!0))},TDMService.getGenericAPI("wsGetAllEnvs").then((function(response){newEnvironmentCtrl.availableSourceEnvironments=_.filter(response.result,(function(env){return!(_.findIndex(newEnvironmentCtrl.environments,{environment_name:env,environment_status:"Active"})>=0)}))})).catch((function(err){toastr.error("New Environment","Unable to get available Source Environment")})),TDMService.getUsersByPermssionGroups("owner").then((function(response){"SUCCESS"==response.errorCode?newEnvironmentCtrl.allOwners=_.map(response.result,item=>(item.user_type||(item.user_type="ID"),item)):(toastr.error("New Environment","failed to get owners: "+response.message),newEnvironmentCtrl.allOwners=[])})),newEnvironmentCtrl.addOwner=function(newOwner){newOwner&&(newEnvironmentCtrl.environmentData.owners||(newEnvironmentCtrl.environmentData.owners=[]),_.findIndex(newEnvironmentCtrl.environmentData.owners,{user_id:newOwner})>=0?newEnvironmentCtrl.addOwnerError=!0:(newEnvironmentCtrl.environmentData.owners.push({displayName:newOwner,uid:newOwner,user_id:newOwner,username:newOwner}),newEnvironmentCtrl.allOwners.push({displayName:newOwner,uid:newOwner,user_id:newOwner,username:newOwner}),newEnvironmentCtrl.isOpen=!1))},newEnvironmentCtrl.initAddNewOwnerPopup=function(){newEnvironmentCtrl.isOpen=!newEnvironmentCtrl.isOpen},newEnvironmentCtrl.closeAddOwnerModal=()=>{newEnvironmentCtrl.isOpen=!1},newEnvironmentCtrl.saveUsersAndGroups=(user,userCustom,userGroup)=>{if(user){const newUser=angular.copy(user);newUser.user_type="ID",newEnvironmentCtrl.environmentData.owners.push(newUser)}userCustom&&(newEnvironmentCtrl.environmentData.owners.push({user_id:userCustom,user_name:userCustom,username:userCustom,user_type:"ID"}),newEnvironmentCtrl.allOwners.push({user_id:userCustom,user_name:userCustom,username:userCustom,user_type:"ID"})),userGroup&&(newEnvironmentCtrl.environmentData.owners.push({user_id:userGroup,user_name:userGroup,username:userGroup,user_type:"GROUP",group:!0}),newEnvironmentCtrl.allOwners.push({user_id:userGroup,user_name:userGroup,username:userGroup,user_type:"GROUP",group:!0})),newEnvironmentCtrl.isOpen=!1},newEnvironmentCtrl.addEnvironment=function(){return newEnvironmentCtrl.environmentData.allow_write||newEnvironmentCtrl.environmentData.allow_read?_.find(newEnvironmentCtrl.environments,{environment_name:newEnvironmentCtrl.environmentData.environment_name,environment_status:"Active"})?toastr.error("Environment # "+newEnvironmentCtrl.environmentData.environment_name+" Already Exists"):void TDMService.addEnvironment(newEnvironmentCtrl.environmentData).then((function(response){"SUCCESS"==response.errorCode?(TDMService.getEnvironment(response.result.id).then((function(response){$timeout((function(){response.result[0].owners=[],response.result[0].user_name=null,$scope.content.openEnvironment(response.result[0])}),300)})),toastr.success("Environment # "+newEnvironmentCtrl.environmentData.environment_name,"Created Successfully")):toastr.error("Environment # "+newEnvironmentCtrl.environmentData.environment_name,"Unable to Create : "+response.message)})):toastr.error("Environment must be Target or Source Environment")},BreadCrumbsService.push({},"NEW_ENVIRONMENT",(function(){}))},controllerAs:"newEnvironmentCtrl"}}angular.module("TDM-FE").directive("newEnvironmentDirective",newEnvironmentDirective);
//# sourceMappingURL=data:application/json;charset=utf8;base64,eyJ2ZXJzaW9uIjozLCJzb3VyY2VzIjpbImRpcmVjdGl2ZXMvZW52aXJvbm1lbnRzL25ld0Vudmlyb25tZW50RGlyZWN0aXZlLmpzIl0sIm5hbWVzIjpbIm5ld0Vudmlyb25tZW50RGlyZWN0aXZlIiwicmVzdHJpY3QiLCJ0ZW1wbGF0ZVVybCIsInNjb3BlIiwiY29udGVudCIsImNvbnRyb2xsZXIiLCIkc2NvcGUiLCJURE1TZXJ2aWNlIiwiQnJlYWRDcnVtYnNTZXJ2aWNlIiwidG9hc3RyIiwiJHRpbWVvdXQiLCJBdXRoU2VydmljZSIsIm5ld0Vudmlyb25tZW50Q3RybCIsInRoaXMiLCJlbnZUeXBlcyIsInN5bmNNb2RlcyIsInRleHQiLCJ2YWx1ZSIsImVudmlyb25tZW50cyIsImdldEZhYnJpY1JvbGVzIiwidGhlbiIsInJlc3BvbnNlIiwidXNlckdyb3VwcyIsInJlc3VsdCIsImVudlR5cGUiLCJlbnZpcm9ubWVudERhdGEiLCJhbGxvd193cml0ZSIsImFsbG93X3JlYWQiLCJvd25lcnMiLCJlbnZUeXBlQ2hhbmdlZCIsInRvTG93ZXJDYXNlIiwiZ2V0R2VuZXJpY0FQSSIsImF2YWlsYWJsZVNvdXJjZUVudmlyb25tZW50cyIsIl8iLCJmaWx0ZXIiLCJlbnYiLCJmaW5kSW5kZXgiLCJlbnZpcm9ubWVudF9uYW1lIiwiZW52aXJvbm1lbnRfc3RhdHVzIiwiY2F0Y2giLCJlcnIiLCJlcnJvciIsImdldFVzZXJzQnlQZXJtc3Npb25Hcm91cHMiLCJlcnJvckNvZGUiLCJhbGxPd25lcnMiLCJtYXAiLCJpdGVtIiwidXNlcl90eXBlIiwibWVzc2FnZSIsImFkZE93bmVyIiwibmV3T3duZXIiLCJ1c2VyX2lkIiwiYWRkT3duZXJFcnJvciIsInB1c2giLCJkaXNwbGF5TmFtZSIsInVpZCIsInVzZXJuYW1lIiwiaXNPcGVuIiwiaW5pdEFkZE5ld093bmVyUG9wdXAiLCJjbG9zZUFkZE93bmVyTW9kYWwiLCJzYXZlVXNlcnNBbmRHcm91cHMiLCJ1c2VyIiwidXNlckN1c3RvbSIsInVzZXJHcm91cCIsIm5ld1VzZXIiLCJhbmd1bGFyIiwiY29weSIsInVzZXJfbmFtZSIsImdyb3VwIiwiYWRkRW52aXJvbm1lbnQiLCJmaW5kIiwiZ2V0RW52aXJvbm1lbnQiLCJpZCIsIm9wZW5FbnZpcm9ubWVudCIsInN1Y2Nlc3MiLCJjb250cm9sbGVyQXMiLCJtb2R1bGUiLCJkaXJlY3RpdmUiXSwibWFwcGluZ3MiOiJBQUFBLFNBQVNBLDBCQXlMTCxNQUFPLENBQ0hDLFNBQVUsSUFDVkMsWUF6TFcseUNBMExYQyxNQUFPLENBQ0hDLFFBQVMsS0FFYkMsV0EzTGEsU0FBVUMsT0FBUUMsV0FBWUMsbUJBQW9CQyxPQUFRQyxTQUFVQyxhQUNqRixJQUFJQyxtQkFBcUJDLEtBQ3pCRCxtQkFBbUJFLFNBQVcsQ0FBQyxPQUFTLFNBQVUsVUFDbERGLG1CQUFtQkcsVUFBWSxDQUMzQixDQUNJQyxLQUFNLGNBQ05DLE1BQU8sT0FFWCxDQUNJRCxLQUFNLGNBQ05DLE1BQU8sVUFHZkwsbUJBQW1CTSxhQUFlWixPQUFPRixRQUFRYyxhQUVqRFgsV0FBV1ksZUFBZSxTQUFTQyxLQUFNQyxXQUNyQ1QsbUJBQW1CVSxXQUFhRCxTQUFTRSxTQUk3Q1gsbUJBQW1CWSxRQUFVLFNBQzdCWixtQkFBbUJhLGdCQUFrQixDQUNqQ0MsYUFBYSxFQUNiQyxZQUFZLEVBQ1pDLE9BQVEsSUFHWmhCLG1CQUFtQmlCLGVBQWlCLFdBQzVCakIsbUJBQW1CWSxVQUM2QixVQUE1Q1osbUJBQW1CWSxRQUFRTSxlQUN2QmxCLG1CQUFtQmEsZ0JBQWdCQyxhQUFjLEVBQ2pEZCxtQkFBbUJhLGdCQUFnQkUsWUFBYSxHQUNELFVBQTVDZixtQkFBbUJZLFFBQVFNLGVBQ2xDbEIsbUJBQW1CYSxnQkFBZ0JDLGFBQWMsRUFDakRkLG1CQUFtQmEsZ0JBQWdCRSxZQUFhLEdBRUMsUUFBNUNmLG1CQUFtQlksUUFBUU0sZ0JBQ2hDbEIsbUJBQW1CYSxnQkFBZ0JDLGFBQWMsRUFDakRkLG1CQUFtQmEsZ0JBQWdCRSxZQUFhLEtBTTVEcEIsV0FBV3dCLGNBQWMsZ0JBQWdCWCxN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T0FBTyxNQUNoRCxRQUVQZCxPQUFPaUUsUUFBUSxpQkFBbUI5RCxtQkFBbUJhLGdCQUFnQlksaUJBQWtCLHlCQUV2RjVCLE9BQU9nQyxNQUFNLGlCQUFtQjdCLG1CQUFtQmEsZ0JBQWdCWSxpQkFBa0Isc0JBQXdCaEIsU0FBUzJCLFlBcEJuSHZDLE9BQU9nQyxNQUFNLHFEQXlCNUJqQyxtQkFBbUI2QyxLQUFLLEdBQUksbUJBQW1CLGdCQVkvQ3NCLGFBQWMsc0JBS3RCVixRQUNLVyxPQUFPLFVBQ1BDLFVBQVUsMEJBQTJCN0UiLCJmaWxlIjoiZGlyZWN0aXZlcy9lbnZpcm9ubWVudHMvbmV3RW52aXJvbm1lbnREaXJlY3RpdmUuanMiLCJzb3VyY2VzQ29udGVudCI6WyJmdW5jdGlvbiBuZXdFbnZpcm9ubWVudERpcmVjdGl2ZSgpIHtcblxuICAgIHZhciB0ZW1wbGF0ZSA9IFwidmlld3MvZW52aXJvbm1lbnRzL25ld0Vudmlyb25tZW50Lmh0bWxcIjtcblxuICAgIHZhciBjb250cm9sbGVyID0gZnVuY3Rpb24gKCRzY29wZSwgVERNU2VydmljZSwgQnJlYWRDcnVtYnNTZXJ2aWNlLCB0b2FzdHIsICR0aW1lb3V0LCBBdXRoU2VydmljZSkge1xuICAgICAgICB2YXIgbmV3RW52aXJvbm1lbnRDdHJsID0gdGhpcztcbiAgICAgICAgbmV3RW52aXJvbm1lbnRDdHJsLmVudlR5cGVzID0gWydCb3RoJyAsICdTb3VyY2UnLCAnVGFyZ2V0J107XG4gICAgICAgIG5ld0Vudmlyb25tZW50Q3RybC5zeW5jTW9kZXMgPSBbXG4gICAgICAgICAgICB7XG4gICAgICAgICAgICAgICAgdGV4dDogJ0RvIG5vdCBTeW5jJyxcbiAgICAgICAgICAgICAgICB2YWx1ZTogJ09GRidcbiAgICAgICAgICAgIH0sXG4gICAgICAgICAgICB7XG4gICAgICAgICAgICAgICAgdGV4dDogJ0Fsd2F5cyBTeW5jJyxcbiAgICAgICAgICAgICAgICB2YWx1ZTogJ0ZPUkNFJ1xuICAgICAgICAgICAgfSxcbiAgICAgICAgXTtcbiAgICAgICAgbmV3RW52aXJvbm1lbnRDdHJsLmVudmlyb25tZW50cyA9ICRzY29wZS5jb250ZW50LmVudmlyb25tZW50cztcblxuICAgICAgICBURE1TZXJ2aWNlLmdldEZhYnJpY1JvbGVzKCdvd25lcicpLnRoZW4oKHJlc3BvbnNlKSA9PiB7XG4gICAgICAgICAgICBuZXdFbnZpcm9ubWVudEN0cmwudXNlckdyb3VwcyA9IHJlc3BvbnNlLnJlc3VsdDtcbiAgICAgICAgfSk7XG5cblxuICAgICAgICBuZXdFbnZpcm9ubWVudEN0cmwuZW52VHlwZSA9ICdUYXJnZXQnO1xuICAgICAgICBuZXdFbnZpcm9ubWVudEN0cmwuZW52aXJvbm1lbnREYXRhID0ge1xuICAgICAgICAgICAgYWxsb3dfd3JpdGU6IHRydWUsXG4gICAgICAgICAgICBhbGxvd19yZWFkOiBmYWxzZSxcbiAgICAgICAgICAgIG93bmVyczogW10sXG4gICAgICAgIH1cblxuICAgICAgICBuZXdFbnZpcm9ubWVudEN0cmwuZW52VHlwZUNoYW5nZWQgPSBmdW5jdGlvbiAoKSB7XG4gICAgICAgICAgICBpZiAobmV3RW52aXJvbm1lbnRDdHJsLmVudlR5cGUpIHtcbiAgICAgICAgICAgICAgICBpZiAobmV3RW52aXJvbm1lbnRDdHJsLmVudlR5cGUudG9Mb3dlckNhc2UoKSA9PSAndGFyZ2V0Jykge1xuICAgICAgICAgICAgICAgICAgICAgICAgbmV3RW52aXJvbm1lbnRDdHJsLmVudmlyb25tZW50RGF0YS5hbGxvd193cml0ZSA9IHRydWU7XG4gICAgICAgICAgICAgICAgICAgICAgICBuZXdFbnZpcm9ubWVudEN0cmwuZW52aXJvbm1lbnREYXRhLmFsbG93X3JlYWQgPSBmYWxzZTtcbiAgICAgICAgICAgICAgICB9IGVsc2UgaWYgKG5ld0Vudmlyb25tZW50Q3RybC5lbnZUeXBlLnRvTG93ZXJDYXNlKCkgPT0gJ3NvdXJjZScpIHtcbiAgICAgICAgICAgICAgICAgICAgbmV3RW52aXJvbm1lbnRDdHJsLmVudmlyb25tZW50RGF0YS5hbGxvd193cml0ZSA9IGZhbHNlO1xuICAgICAgICAgICAgICAgICAgICBuZXdFbnZpcm9ubWVudEN0cmwuZW52aXJvbm1lbnREYXRhLmFsbG93X3JlYWQgPSB0cnVlO1xuICAgICAgICAgICAgICAgIH1cbiAgICAgICAgICAgICAgICBlbHNlIGlmIChuZXdFbnZpcm9ubWVudEN0cmwuZW52VHlwZS50b0xvd2VyQ2FzZSgpID09ICdib3RoJykge1xuICAgICAgICAgICAgICAgICAgICBuZXdFbnZpcm9ubWVudEN0cmwuZW52aXJvbm1lbnREYXRhLmFsbG93X3dyaXRlID0gdHJ1ZTtcbiAgICAgICAgICAgICAgICAgICAgbmV3RW52aXJvbm1lbnRDdHJsLmVudmlyb25tZW50RGF0YS5hbGxvd19yZWFkID0gdHJ1ZTtcbiAgICAgICAgICAgICAgICB9XG4gICAgICAgICAgICB9XG4gICAgICAgIH07XG5cblxuICAgICAgICBURE1TZXJ2aWNlLmdldEdlbmVyaWNBUEkoJ3dzR2V0QWxsRW52cycpLnRoZW4oZnVuY3Rpb24gKHJlc3BvbnNlKSB7XG4gICAgICAgICAgICBuZXdFbnZpcm9ubWVudEN0cmwuYXZhaWxhYmxlU291cmNlRW52aXJvbm1lbnRzID0gXy5maWx0ZXIocmVzcG9uc2UucmVzdWx0LCBmdW5jdGlvbiAoZW52KSB7XG4gICAgICAgICAgICAgICAgaWYgKF8uZmluZEluZGV4KG5ld0Vudmlyb25tZW50Q3RybC5lbnZpcm9ubWVudHMsIHtcbiAgICAgICAgICAgICAgICAgICAgZW52aXJvbm1lbnRfbmFtZTogZW52LFxuICAgICAgICAgICAgICAgICAgICBlbnZpcm9ubWVudF9zdGF0dXM6ICdBY3RpdmUnXG4gICAgICAgICAgICAgICAgfSkgPj0gMCkge1xuICAgICAgICAgICAgICAgICAgICByZXR1cm4gZmFsc2U7XG4gICAgICAgICAgICAgICAgfVxuICAgICAgICAgICAgICAgIHJldHVybiB0cnVlO1xuICAgICAgICAgICAgfSk7XG4gICAgICAgIH0pLmNhdGNoKGZ1bmN0aW9uIChlcnIpIHtcbiAgICAgICAgICAgIHRvYXN0ci5lcnJvcihcIk5ldyBFbnZpcm9ubWVudFwiLCBcIlVuYWJsZSB0byBnZXQgYXZhaWxhYmxlIFNvdXJjZSBFbnZpcm9ubWVudFwiKTtcbiAgICAgICAgfSk7XG5cbiAgICAgICAgVERNU2VydmljZS5nZXRVc2Vyc0J5UGVybXNzaW9uR3JvdXBzKCdvd25lcicpLnRoZW4oZnVuY3Rpb24gKHJlc3BvbnNlKSB7XG4gICAgICAgICAgICBpZiAocmVzcG9uc2UuZXJyb3JDb2RlID09IFwiU1VDQ0VTU1wiKSB7XG4gICAgICAgICAgICAgICAgbmV3RW52aXJvbm1lbnRDdHJsLmFsbE93bmVycyA9IF8ubWFwKHJlc3BvbnNlLnJlc3VsdCxpdGVtID0+IHtcbiAgICAgICAgICAgICAgICAgICAgaWYgKCFpdGVtLnVzZXJfdHlwZSkge1xuICAgICAgICAgICAgICAgICAgICAgICAgaXRlbS51c2VyX3R5cGUgPSAnSUQnO1xuICAgICAgICAgICAgICAgICAgICB9XG4gICAgICAgICAgICAgICAgICAgIHJldHVybiBpdGVtO1xuICAgICAgICAgICAgICAgIH0pO1xuICAgICAgICAgICAgfSBlbHNlIHtcbiAgICAgICAgICAgICAgICB0b2FzdHIuZXJyb3IoXCJOZXcgRW52aXJvbm1lbnRcIiwgXCJmYWlsZWQgdG8gZ2V0IG93bmVyczogXCIgKyByZXNwb25zZS5tZXNzYWdlKTtcbiAgICAgICAgICAgICAgICBuZXdFbnZpcm9ubWVudEN0cmwuYWxsT3duZXJzID0gW107XG4gICAgICAgICAgICB9XG4gICAgICAgIH0pO1xuXG4gICAgICAgIG5ld0Vudmlyb25tZW50Q3RybC5hZGRPd25lciA9IGZ1bmN0aW9uKG5ld093bmVyKXtcbiAgICAgICAgICAgIGlmIChuZXdPd25lcil7XG4gICAgICAgICAgICAgICAgaWYgKCFuZXdFbnZpcm9ubWVudEN0cmwuZW52aXJvbm1lbnREYXRhLm93bmVycyl7XG4gICAgICAgICAgICAgICAgICAgIG5ld0Vudmlyb25tZW50Q3RybC5lbnZpcm9ubWVudERhdGEub3duZXJzID0gW107XG4gICAgICAgICAgICAgICAgfVxuICAgICAgICAgICAgICAgIGlmIChfLmZpbmRJbmRleChuZXdFbnZpcm9ubWVudEN0cmwuZW52aXJvbm1lbnREYXRhLm93bmVycyx7dXNlcl9pZDogbmV3T3duZXJ9KSA+PSAwKXtcbiAgICAgICAgICAgICAgICAgICAgbmV3RW52aXJvbm1lbnRDdHJsLmFkZE93bmVyRXJyb3IgPSB0cnVlO1xuICAgICAgICAgICAgICAgIH1cbiAgICAgICAgICAgICAgICBlbHNle1xuICAgICAgICAgICAgICAgICAgICBuZXdFbnZpcm9ubWVudEN0cmwuZW52aXJvbm1lbnREYXRhLm93bmVycy5wdXNoKHtcbiAgICAgICAgICAgICAgICAgICAgICAgIGRpc3BsYXlOYW1lOiBuZXdPd25lcixcbiAgICAgICAgICAgICAgICAgICAgICAgIHVpZDogbmV3T3duZXIsXG4gICAgICAgICAgICAgICAgICAgICAgICB1c2VyX2lkOiBuZXdPd25lcixcbiAgICAgICAgICAgICAgICAgICAgICAgIHVzZXJuYW1lOiBuZXdPd25lcixcbiAgICAgICAgICAgICAgICAgICAgfSk7XG4gICAgICAgICAgICAgICAgICAgIG5ld0Vudmlyb25tZW50Q3RybC5hbGxPd25lcnMucHVzaCh7XG4gICAgICAgICAgICAgICAgICAgICAgICBkaXNwbGF5TmFtZTogbmV3T3duZXIsXG4gICAgICAgICAgICAgICAgICAgICAgICB1aWQ6IG5ld093bmVyLFxuICAgICAgICAgICAgICAgICAgICAgICAgdXNlcl9pZDogbmV3T3duZXIsXG4gICAgICAgICAgICAgICAgICAgICAgICB1c2VybmFtZTogbmV3T3duZXIsXG4gICAgICAgICAgICAgICAgICAgIH0pO1xuICAgICAgICAgICAgICAgICAgICBuZXdFbnZpcm9ubWVudEN0cmwuaXNPcGVuID0gZmFsc2U7XG4gICAgICAgICAgICAgICAgfVxuICAgICAgICAgICAgfVxuICAgICAgICB9O1xuXG4gICAgICAgIG5ld0Vudmlyb25tZW50Q3RybC5pbml0QWRkTmV3T3duZXJQb3B1cCA9IGZ1bmN0aW9uKCl7XG4gICAgICAgICAgICBuZXdFbnZpcm9ubWVudEN0cmwuaXNPcGVuID0gIW5ld0Vudmlyb25tZW50Q3RybC5pc09wZW47XG4gICAgICAgIH07XG5cbiAgICAgICAgXG4gICAgICAgIG5ld0Vudmlyb25tZW50Q3RybC5jbG9zZUFkZE93bmVyTW9kYWwgPSAoKSA9PiB7XG4gICAgICAgICAgICBuZXdFbnZpcm9ubWVudEN0cmwuaXNPcGVuID0gZmFsc2VcbiAgICAgICAgfVxuXG5cbiAgICAgICAgbmV3RW52aXJvbm1lbnRDdHJsLnNhdmVVc2Vyc0FuZEdyb3VwcyA9ICh1c2VyLHVzZXJDdXN0b20sdXNlckdyb3VwKSA9PiB7XG4gICAgICAgICAgICBpZiAodXNlcikge1xuICAgICAgICAgICAgICAgIGNvbnN0IG5ld1VzZXIgPSBhbmd1bGFyLmNvcHkodXNlcik7XG4gICAgICAgICAgICAgICAgbmV3VXNlci51c2VyX3R5cGUgPSAnSUQnO1xuICAgICAgICAgICAgICAgIG5ld0Vudmlyb25tZW50Q3RybC5lbnZpcm9ubWVudERhdGEub3duZXJzLnB1c2gobmV3VXNlcik7XG4gICAgICAgICAgICB9XG4gICAgICAgICAgICBpZiAodXNlckN1c3RvbSkge1xuICAgICAgICAgICAgICAgIG5ld0Vudmlyb25tZW50Q3RybC5lbnZpcm9ubWVudERhdGEub3duZXJzLnB1c2goe1xuICAgICAgICAgICAgICAgICAgICB1c2VyX2lkOiB1c2VyQ3VzdG9tLFxuICAgICAgICAgICAgICAgICAgICB1c2VyX25hbWU6IHVzZXJDdXN0b20sXG4gICAgICAgICAgICAgICAgICAgIHVzZXJuYW1lOiB1c2VyQ3VzdG9tLFxuICAgICAgICAgICAgICAgICAgICB1c2VyX3R5cGU6ICdJRCcsXG4gICAgICAgICAgICAgICAgfSk7XG4gICAgICAgICAgICAgICAgbmV3RW52aXJvbm1lbnRDdHJsLmFsbE93bmVycy5wdXNoKHtcbiAgICAgICAgICAgICAgICAgICAgdXNlcl9pZDogdXNlckN1c3RvbSxcbiAgICAgICAgICAgICAgICAgICAgdXNlcl9uYW1lOiB1c2VyQ3VzdG9tLFxuICAgICAgICAgICAgICAgICAgICB1c2VybmFtZTogdXNlckN1c3RvbSxcbiAgICAgICAgICAgICAgICAgICAgdXNlcl90eXBlOiAnSUQnLFxuICAgICAgICAgICAgICAgIH0pO1xuICAgICAgICAgICAgfVxuICAgICAgICAgICAgaWYgKHVzZXJHcm91cCkge1xuICAgICAgICAgICAgICAgIG5ld0Vudmlyb25tZW50Q3RybC5lbnZpcm9ubWVudERhdGEub3duZXJzLnB1c2goe1xuICAgICAgICAgICAgICAgICAgICB1c2VyX2lkOiB1c2VyR3JvdXAsXG4gICAgICAgICAgICAgICAgICAgIHVzZXJfbmFtZTogdXNlckdyb3VwLFxuICAgICAgICAgICAgICAgICAgICB1c2VybmFtZTogdXNlckdyb3VwLFxuICAgICAgICAgICAgICAgICAgICB1c2VyX3R5cGU6ICdHUk9VUCcsXG4gICAgICAgICAgICAgICAgICAgIGdyb3VwOiB0cnVlLFxuICAgICAgICAgICAgICAgIH0pO1xuICAgICAgICAgICAgICAgIG5ld0Vudmlyb25tZW50Q3RybC5hbGxPd25lcnMucHVzaCh7XG4gICAgICAgICAgICAgICAgICAgIHVzZXJfaWQ6IHVzZXJHcm91cCxcbiAgICAgICAgICAgICAgICAgICAgdXNlcl9uYW1lOiB1c2VyR3JvdXAsXG4gICAgICAgICAgICAgICAgICAgIHVzZXJuYW1lOiB1c2VyR3JvdXAsXG4gICAgICAgICAgICAgICAgICAgIHVzZXJfdHlwZTogJ0dST1VQJyxcbiAgICAgICAgICAgICAgICAgICAgZ3JvdXA6IHRydWUsXG4gICAgICAgICAgICAgICAgfSk7XG4gICAgICAgICAgICB9XG4gICAgICAgICAgICBuZXdFbnZpcm9ubWVudEN0cmwuaXNPcGVuID0gZmFsc2U7XG4gICAgICAgIH07XG4gICAgICAgIFxuXG4gICAgICAgIG5ld0Vudmlyb25tZW50Q3RybC5hZGRFbnZpcm9ubWVudCA9IGZ1bmN0aW9uICgpIHtcbiAgICAgICAgICAgIGlmICghbmV3RW52aXJvbm1lbnRDdHJsLmVudmlyb25tZW50RGF0YS5hbGxvd193cml0ZSAmJiBcbiAgICAgICAgICAgICAgICAhbmV3RW52aXJvbm1lbnRDdHJsLmVudmlyb25tZW50RGF0YS5hbGxvd19yZWFkKSB7XG4gICAgICAgICAgICAgICAgcmV0dXJuIHRvYXN0ci5lcnJvcihcIkVudmlyb25tZW50IG11c3QgYmUgVGFyZ2V0IG9yIFNvdXJjZSBFbnZpcm9ubWVudFwiKTtcbiAgICAgICAgICAgIH1cbiAgICAgICAgICAgIGlmIChfLmZpbmQobmV3RW52aXJvbm1lbnRDdHJsLmVudmlyb25tZW50cywge1xuICAgICAgICAgICAgICAgIGVudmlyb25tZW50X25hbWU6IG5ld0Vudmlyb25tZW50Q3RybC5lbnZpcm9ubWVudERhdGEuZW52aXJvbm1lbnRfbmFtZSxcbiAgICAgICAgICAgICAgICBlbnZpcm9ubWVudF9zdGF0dXM6ICdBY3RpdmUnXG4gICAgICAgICAgICB9KSkge1xuICAgICAgICAgICAgICAgIHJldHVybiB0b2FzdHIuZXJyb3IoXCJFbnZpcm9ubWVudCAjIFwiICsgbmV3RW52aXJvbm1lbnRDdHJsLmVudmlyb25tZW50RGF0YS5lbnZpcm9ubWVudF9uYW1lICsgXCIgQWxyZWFkeSBFeGlzdHNcIik7XG4gICAgICAgICAgICB9XG4gICAgICAgICAgICAvLyBuZXdFbnZpcm9ubWVudEN0cmwuZW52aXJvbm1lbnREYXRhLmVudmlyb25tZW50X25hbWUgPSBuZXdFbnZpcm9ubWVudEN0cmwuZW52aXJvbm1lbnREYXRhLmVudmlyb25tZW50X25hbWU7XG4gICAgICAgICAgICBURE1TZXJ2aWNlLmFkZEVudmlyb25tZW50KG5ld0Vudmlyb25tZW50Q3RybC5lbnZpcm9ubWVudERhdGEpLnRoZW4oZnVuY3Rpb24gKHJlc3BvbnNlKSB7XG4gICAgICAgICAgICAgICAgaWYgKHJlc3BvbnNlLmVycm9yQ29kZSA9PSBcIlNVQ0NFU1NcIikge1xuICAgICAgICAgICAgICAgICAgICBURE1TZXJ2aWNlLmdldEVudmlyb25tZW50KHJlc3BvbnNlLnJlc3VsdC5pZCkudGhlbihmdW5jdGlvbiAocmVzcG9uc2UpIHtcbiAgICAgICAgICAgICAgICAgICAgICAgICR0aW1lb3V0KGZ1bmN0aW9uICgpIHtcbiAgICAgICAgICAgICAgICAgICAgICAgICAgICByZXNwb25zZS5yZXN1bHRbMF0ub3duZXJzID0gW107XG4gICAgICAgICAgICAgICAgICAgICAgICAgICAgcmVzcG9uc2UucmVzdWx0WzBdLnVzZXJfbmFtZSA9IG51bGw7XG4gICAgICAgICAgICAgICAgICAgICAgICAgICAgJHNjb3BlLmNvbnRlbnQub3BlbkVudmlyb25tZW50KHJlc3BvbnNlLnJlc3VsdFswXSk7XG4gICAgICAgICAgICAgICAgICAgICAgICB9LCAzMDApO1xuICAgICAgICAgICAgICAgICAgICB9KTtcbiAgICAgICAgICAgICAgICAgICAgdG9hc3RyLnN1Y2Nlc3MoXCJFbnZpcm9ubWVudCAjIFwiICsgbmV3RW52aXJvbm1lbnRDdHJsLmVudmlyb25tZW50RGF0YS5lbnZpcm9ubWVudF9uYW1lLCBcIkNyZWF0ZWQgU3VjY2Vzc2Z1bGx5XCIpO1xuICAgICAgICAgICAgICAgIH0gZWxzZSB7XG4gICAgICAgICAgICAgICAgICAgIHRvYXN0ci5lcnJvcihcIkVudmlyb25tZW50ICMgXCIgKyBuZXdFbnZpcm9ubWVudEN0cmwuZW52aXJvbm1lbnREYXRhLmVudmlyb25tZW50X25hbWUsIFwiVW5hYmxlIHRvIENyZWF0ZSA6IFwiICsgcmVzcG9uc2UubWVzc2FnZSk7XG4gICAgICAgICAgICAgICAgfVxuICAgICAgICAgICAgfSk7XG4gICAgICAgIH07XG5cbiAgICAgICAgQnJlYWRDcnVtYnNTZXJ2aWNlLnB1c2goe30sICdORVdfRU5WSVJPTk1FTlQnLCBmdW5jdGlvbiAoKSB7XG5cbiAgICAgICAgfSk7XG4gICAgfTtcblxuICAgIHJldHVybiB7XG4gICAgICAgIHJlc3RyaWN0OiBcIkVcIixcbiAgICAgICAgdGVtcGxhdGVVcmw6IHRlbXBsYXRlLFxuICAgICAgICBzY29wZToge1xuICAgICAgICAgICAgY29udGVudDogJz0nXG4gICAgICAgIH0sXG4gICAgICAgIGNvbnRyb2xsZXI6IGNvbnRyb2xsZXIsXG4gICAgICAgIGNvbnRyb2xsZXJBczogJ25ld0Vudmlyb25tZW50Q3RybCdcbiAgICB9O1xufVxuXG5cbmFuZ3VsYXJcbiAgICAubW9kdWxlKCdURE0tRkUnKVxuICAgIC5kaXJlY3RpdmUoJ25ld0Vudmlyb25tZW50RGlyZWN0aXZlJywgbmV3RW52aXJvbm1lbnREaXJlY3RpdmUpOyJdfQ==