angular.module("TDM-FE").filter("getAvailableLogicalUnits",(function(){return function(logicalUnits,chosen,lu_name){return _.filter(logicalUnits,(function(logicalUnit){return!(!lu_name||lu_name!==logicalUnit.logical_unit)||!_.find(chosen,{lu_name:logicalUnit.logical_unit})}))}})).filter("getAvailableParentLogicalUnits",(function(){return function(logicalUnits,chosen,currentIndex){return available=_.filter(logicalUnits,(function(logicalUnit){return!!logicalUnit.lu_id||(!chosen[currentIndex].lu_name||chosen[currentIndex].lu_name!==logicalUnit.logical_unit)&&!!_.find(chosen,{lu_name:logicalUnit.logical_unit})})),available}})).filter("checkIfLogicalUnitIsParent",(function(){return function(logicalUnits,index){return!(index>=logicalUnits.length)&&!(_.findIndex(logicalUnits,{lu_parent_id:logicalUnits[index].lu_id.toString()})>=0)}})).filter("atLeastOneInterface",(function(){return function(interfaces){for(var ans=0,i=0;i<interfaces.length;i++)interfaces[i].deleted||ans++;return ans>1}})).filter("isSelectionAvailable",(function(){return function(data){if(!data)return!1;const keys=Object.keys(data);for(let i=0;i<keys.length;i++)if(data[keys[i]])return!0;return!1}}));
//# sourceMappingURL=data:application/json;charset=utf8;base64,eyJ2ZXJzaW9uIjozLCJzb3VyY2VzIjpbImZpbHRlcnMvZmlsdGVycy5qcyJdLCJuYW1lcyI6WyJhbmd1bGFyIiwibW9kdWxlIiwiZmlsdGVyIiwibG9naWNhbFVuaXRzIiwiY2hvc2VuIiwibHVfbmFtZSIsIl8iLCJsb2dpY2FsVW5pdCIsImxvZ2ljYWxfdW5pdCIsImZpbmQiLCJjdXJyZW50SW5kZXgiLCJhdmFpbGFibGUiLCJsdV9pZCIsImluZGV4IiwibGVuZ3RoIiwiZmluZEluZGV4IiwibHVfcGFyZW50X2lkIiwidG9TdHJpbmciLCJpbnRlcmZhY2VzIiwiYW5zIiwiaSIsImRlbGV0ZWQiLCJkYXRhIiwia2V5cyIsIk9iamVjdCJdLCJtYXBwaW5ncyI6IkFBQUFBLFFBQ0tDLE9BQU8sVUFBVUMsT0FBTyw0QkFBNEIsV0FDckQsT0FBTyxTQUFVQyxhQUFhQyxPQUFPQyxTQVdqQyxPQVZnQkMsRUFBRUosT0FBT0MsY0FBYSxTQUFTSSxhQUMzQyxTQUFJRixTQUFXQSxVQUFZRSxZQUFZQyxnQkFHM0JGLEVBQUVHLEtBQUtMLE9BQVEsQ0FBRUMsUUFBVUUsWUFBWUMsc0JBUTVETixPQUFPLGtDQUFrQyxXQUN4QyxPQUFPLFNBQVVDLGFBQWFDLE9BQU9NLGNBY2pDLE9BYkFDLFVBQVlMLEVBQUVKLE9BQU9DLGNBQWEsU0FBU0ksYUFDdkMsUUFBSUEsWUFBWUssU0FHWlIsT0FBT00sY0FBY0wsU0FBWUQsT0FBT00sY0FBY0wsVUFBWUUsWUFBWUMsaUJBR3RFRixFQUFFRyxLQUFLTCxPQUFRLENBQUVDLFFBQVVFLFlBQVlDLGtCQU1oREcsY0FFWlQsT0FBTyw4QkFBOEIsV0FDcEMsT0FBTyxTQUFVQyxhQUFhVSxPQUMxQixRQUFJQSxPQUFTVixhQUFhVyxXQUd0QlIsRUFBRVMsVUFBVVosYUFBYSxDQUFDYSxhQUFlYixhQUFhVSxPQUFPRCxNQUFNSyxjQUFnQixPQUs1RmYsT0FBTyx1QkFBdUIsV0FDN0IsT0FBTyxTQUFVZ0IsWUFFYixJQURBLElBQUlDLElBQU0sRUFDREMsRUFBSSxFQUFHQSxFQUFJRixXQUFXSixPQUFTTSxJQUMvQkYsV0FBV0UsR0FBR0MsU0FDZkYsTUFHUixPQUFRQSxJQUFNLE1BRW5CakIsT0FBTyx3QkFBd0IsV0FDOUIsT0FBTyxTQUFVb0IsTUFDYixJQUFLQSxLQUNELE9BQU8sRUFFWCxNQUFNQyxLQUFPQyxPQUFPRCxLQUFLRCxNQUN6QixJQUFJLElBQUlGLEVBQUcsRUFBR0EsRUFBSUcsS0FBS1QsT0FBUU0sSUFDM0IsR0FBSUUsS0FBS0MsS0FBS0gsSUFDVixPQUFPLEVBR2YsT0FBTyIsImZpbGUiOiJmaWx0ZXJzL2ZpbHRlcnMuanMiLCJzb3VyY2VzQ29udGVudCI6WyJhbmd1bGFyXG4gICAgLm1vZHVsZSgnVERNLUZFJykuZmlsdGVyKCdnZXRBdmFpbGFibGVMb2dpY2FsVW5pdHMnLCBmdW5jdGlvbiAoKSB7XG4gICAgcmV0dXJuIGZ1bmN0aW9uIChsb2dpY2FsVW5pdHMsY2hvc2VuLGx1X25hbWUpIHtcbiAgICAgICAgdmFyIGF2YWlsYWJsZSA9IF8uZmlsdGVyKGxvZ2ljYWxVbml0cyxmdW5jdGlvbihsb2dpY2FsVW5pdCl7XG4gICAgICAgICAgICBpZiAobHVfbmFtZSAmJiBsdV9uYW1lID09PSBsb2dpY2FsVW5pdC5sb2dpY2FsX3VuaXQpe1xuICAgICAgICAgICAgICAgIHJldHVybiB0cnVlO1xuICAgICAgICAgICAgfVxuICAgICAgICAgICAgdmFyIGZvdW5kID0gXy5maW5kKGNob3NlbiwgeyBsdV9uYW1lIDogbG9naWNhbFVuaXQubG9naWNhbF91bml0fSk7XG4gICAgICAgICAgICBpZiAoZm91bmQpe1xuICAgICAgICAgICAgICAgIHJldHVybiBmYWxzZTtcbiAgICAgICAgICAgIH1cbiAgICAgICAgICAgIHJldHVybiB0cnVlO1xuICAgICAgICB9KTtcbiAgICAgICAgcmV0dXJuIGF2YWlsYWJsZTtcbiAgICB9XG59KS5maWx0ZXIoJ2dldEF2YWlsYWJsZVBhcmVudExvZ2ljYWxVbml0cycsIGZ1bmN0aW9uICgpIHtcbiAgICByZXR1cm4gZnVuY3Rpb24gKGxvZ2ljYWxVbml0cyxjaG9zZW4sY3VycmVudEluZGV4KSB7XG4gICAgICAgIGF2YWlsYWJsZSA9IF8uZmlsdGVyKGxvZ2ljYWxVbml0cyxmdW5jdGlvbihsb2dpY2FsVW5pdCl7XG4gICAgICAgICAgICBpZiAobG9naWNhbFVuaXQubHVfaWQpe1xuICAgICAgICAgICAgICAgIHJldHVybiB0cnVlO1xuICAgICAgICAgICAgfVxuICAgICAgICAgICAgaWYgKGNob3NlbltjdXJyZW50SW5kZXhdLmx1X25hbWUgJiYgIGNob3NlbltjdXJyZW50SW5kZXhdLmx1X25hbWUgPT09IGxvZ2ljYWxVbml0LmxvZ2ljYWxfdW5pdCl7XG4gICAgICAgICAgICAgICAgcmV0dXJuIGZhbHNlO1xuICAgICAgICAgICAgfVxuICAgICAgICAgICAgdmFyIGZvdW5kID0gXy5maW5kKGNob3NlbiwgeyBsdV9uYW1lIDogbG9naWNhbFVuaXQubG9naWNhbF91bml0fSk7XG4gICAgICAgICAgICBpZiAoZm91bmQpe1xuICAgICAgICAgICAgICAgIHJldHVybiB0cnVlO1xuICAgICAgICAgICAgfVxuICAgICAgICAgICAgcmV0dXJuIGZhbHNlO1xuICAgICAgICB9KTtcbiAgICAgICAgcmV0dXJuIGF2YWlsYWJsZTtcbiAgICB9XG59KS5maWx0ZXIoJ2NoZWNrSWZMb2dpY2FsVW5pdElzUGFyZW50JywgZnVuY3Rpb24gKCkge1xuICAgIHJldHVybiBmdW5jdGlvbiAobG9naWNhbFVuaXRzLGluZGV4KSB7XG4gICAgICAgIGlmIChpbmRleCA+PSBsb2dpY2FsVW5pdHMubGVuZ3RoKXtcbiAgICAgICAgICAgIHJldHVybiBmYWxzZTtcbiAgICAgICAgfVxuICAgICAgICBpZiAoXy5maW5kSW5kZXgobG9naWNhbFVuaXRzLHtsdV9wYXJlbnRfaWQgOiBsb2dpY2FsVW5pdHNbaW5kZXhdLmx1X2lkLnRvU3RyaW5nKCl9KSA+PSAwKXtcbiAgICAgICAgICAgIHJldHVybiBmYWxzZTtcbiAgICAgICAgfVxuICAgICAgICByZXR1cm4gdHJ1ZTtcbiAgICB9XG59KS5maWx0ZXIoJ2F0TGVhc3RPbmVJbnRlcmZhY2UnLCBmdW5jdGlvbiAoKSB7XG4gICAgcmV0dXJuIGZ1bmN0aW9uIChpbnRlcmZhY2VzKSB7XG4gICAgICAgIHZhciBhbnMgPSAwO1xuICAgICAgICBmb3IgKHZhciBpID0gMDsgaSA8IGludGVyZmFjZXMubGVuZ3RoIDsgaSsrKXtcbiAgICAgICAgICAgIGlmICghaW50ZXJmYWNlc1tpXS5kZWxldGVkKXtcbiAgICAgICAgICAgICAgICBhbnMrKztcbiAgICAgICAgICAgIH1cbiAgICAgICAgfVxuICAgICAgICByZXR1cm4gKGFucyA+IDEpO1xuICAgIH1cbn0pLmZpbHRlcignaXNTZWxlY3Rpb25BdmFpbGFibGUnLCBmdW5jdGlvbiAoKSB7XG4gICAgcmV0dXJuIGZ1bmN0aW9uIChkYXRhKSB7XG4gICAgICAgIGlmICghZGF0YSkge1xuICAgICAgICAgICAgcmV0dXJuIGZhbHNlO1xuICAgICAgICB9XG4gICAgICAgIGNvbnN0IGtleXMgPSBPYmplY3Qua2V5cyhkYXRhKTtcbiAgICAgICAgZm9yKGxldCBpID0wOyBpIDwga2V5cy5sZW5ndGg7IGkrKykge1xuICAgICAgICAgICAgaWYgKGRhdGFba2V5c1tpXV0peyBcbiAgICAgICAgICAgICAgICByZXR1cm4gdHJ1ZTtcbiAgICAgICAgICAgIH1cbiAgICAgICAgfVxuICAgICAgICByZXR1cm4gZmFsc2U7XG4gICAgfVxufSk7XG4iXX0=
