function permissionGroupsCtrl($scope,BreadCrumbsService){var permissionGroupsCtrl=this;permissionGroupsCtrl.pageDisplay="permissionGroupsTable",permissionGroupsCtrl.openPermissionGroups=function(){permissionGroupsCtrl.productsData={openProduct:permissionGroupsCtrl.openProduct,openNewProduct:permissionGroupsCtrl.openNewProduct},permissionGroupsCtrl.pageDisplay="permissionGroupsTable",BreadCrumbsService.breadCrumbChange(1)},permissionGroupsCtrl.openProduct=function(productData){BreadCrumbsService.breadCrumbChange(1),permissionGroupsCtrl.productData={productData:productData,openProducts:permissionGroupsCtrl.openProducts},permissionGroupsCtrl.pageDisplay="product"},permissionGroupsCtrl.openNewProduct=function(productData){permissionGroupsCtrl.newProductData={openProduct:permissionGroupsCtrl.openProduct,productData:productData},permissionGroupsCtrl.pageDisplay="newProduct"},BreadCrumbsService.breadCrumbChange(0),BreadCrumbsService.push({},"PERMISSION_GROUPS",(function(){permissionGroupsCtrl.openProducts()})),permissionGroupsCtrl.productsData={openProduct:permissionGroupsCtrl.openProduct,openNewProduct:permissionGroupsCtrl.openNewProduct},permissionGroupsCtrl.pageDisplay="permissionGroupsTable"}angular.module("TDM-FE").controller("permissionGroupsCtrl",permissionGroupsCtrl);
//# sourceMappingURL=data:application/json;charset=utf8;base64,eyJ2ZXJzaW9uIjozLCJzb3VyY2VzIjpbImNvbnRyb2xsZXJzL3Blcm1pc3Npb25Hcm91cHNDdHJsLmpzIl0sIm5hbWVzIjpbInBlcm1pc3Npb25Hcm91cHNDdHJsIiwiJHNjb3BlIiwiQnJlYWRDcnVtYnNTZXJ2aWNlIiwidGhpcyIsInBhZ2VEaXNwbGF5Iiwib3BlblBlcm1pc3Npb25Hcm91cHMiLCJwcm9kdWN0c0RhdGEiLCJvcGVuUHJvZHVjdCIsIm9wZW5OZXdQcm9kdWN0IiwiYnJlYWRDcnVtYkNoYW5nZSIsInByb2R1Y3REYXRhIiwib3BlblByb2R1Y3RzIiwibmV3UHJvZHVjdERhdGEiLCJwdXNoIiwiYW5ndWxhciIsIm1vZHVsZSIsImNvbnRyb2xsZXIiXSwibWFwcGluZ3MiOiJBQUFBLFNBQVNBLHFCQUFzQkMsT0FBT0Msb0JBQ2xDLElBQUlGLHFCQUF1QkcsS0FDM0JILHFCQUFxQkksWUFBYyx3QkFFbkNKLHFCQUFxQksscUJBQXVCLFdBQ3hDTCxxQkFBcUJNLGFBQWUsQ0FDaENDLFlBQWNQLHFCQUFxQk8sWUFDbkNDLGVBQWlCUixxQkFBcUJRLGdCQUUxQ1IscUJBQXFCSSxZQUFjLHdCQUNuQ0YsbUJBQW1CTyxpQkFBaUIsSUFHeENULHFCQUFxQk8sWUFBYyxTQUFTRyxhQUN4Q1IsbUJBQW1CTyxpQkFBaUIsR0FDcENULHFCQUFxQlUsWUFBYyxDQUMvQkEsWUFBY0EsWUFDZEMsYUFBZVgscUJBQXFCVyxjQUV4Q1gscUJBQXFCSSxZQUFjLFdBR3ZDSixxQkFBcUJRLGVBQWlCLFNBQVNFLGFBQzNDVixxQkFBcUJZLGVBQWlCLENBQ2xDTCxZQUFjUCxxQkFBcUJPLFlBQ25DRyxZQUFjQSxhQUVsQlYscUJBQXFCSSxZQUFjLGNBR3ZDRixtQkFBbUJPLGlCQUFpQixHQUNwQ1AsbUJBQW1CVyxLQUFLLEdBQUcscUJBQW9CLFdBQzNDYixxQkFBcUJXLGtCQUd6QlgscUJBQXFCTSxhQUFlLENBQ2hDQyxZQUFjUCxxQkFBcUJPLFlBQ25DQyxlQUFpQlIscUJBQXFCUSxnQkFFMUNSLHFCQUFxQkksWUFBYyx3QkFHdkNVLFFBQ0tDLE9BQU8sVUFDUEMsV0FBVyx1QkFBeUJoQiIsImZpbGUiOiJjb250cm9sbGVycy9wZXJtaXNzaW9uR3JvdXBzQ3RybC5qcyIsInNvdXJjZXNDb250ZW50IjpbImZ1bmN0aW9uIHBlcm1pc3Npb25Hcm91cHNDdHJsICgkc2NvcGUsQnJlYWRDcnVtYnNTZXJ2aWNlKXtcbiAgICB2YXIgcGVybWlzc2lvbkdyb3Vwc0N0cmwgPSB0aGlzO1xuICAgIHBlcm1pc3Npb25Hcm91cHNDdHJsLnBhZ2VEaXNwbGF5ID0gJ3Blcm1pc3Npb25Hcm91cHNUYWJsZSc7XG5cbiAgICBwZXJtaXNzaW9uR3JvdXBzQ3RybC5vcGVuUGVybWlzc2lvbkdyb3VwcyA9IGZ1bmN0aW9uKCl7XG4gICAgICAgIHBlcm1pc3Npb25Hcm91cHNDdHJsLnByb2R1Y3RzRGF0YSA9IHtcbiAgICAgICAgICAgIG9wZW5Qcm9kdWN0IDogcGVybWlzc2lvbkdyb3Vwc0N0cmwub3BlblByb2R1Y3QsXG4gICAgICAgICAgICBvcGVuTmV3UHJvZHVjdCA6IHBlcm1pc3Npb25Hcm91cHNDdHJsLm9wZW5OZXdQcm9kdWN0XG4gICAgICAgIH07XG4gICAgICAgIHBlcm1pc3Npb25Hcm91cHNDdHJsLnBhZ2VEaXNwbGF5ID0gJ3Blcm1pc3Npb25Hcm91cHNUYWJsZSc7XG4gICAgICAgIEJyZWFkQ3J1bWJzU2VydmljZS5icmVhZENydW1iQ2hhbmdlKDEpO1xuICAgIH07XG5cbiAgICBwZXJtaXNzaW9uR3JvdXBzQ3RybC5vcGVuUHJvZHVjdCA9IGZ1bmN0aW9uKHByb2R1Y3REYXRhKXtcbiAgICAgICAgQnJlYWRDcnVtYnNTZXJ2aWNlLmJyZWFkQ3J1bWJDaGFuZ2UoMSk7XG4gICAgICAgIHBlcm1pc3Npb25Hcm91cHNDdHJsLnByb2R1Y3REYXRhID0ge1xuICAgICAgICAgICAgcHJvZHVjdERhdGEgOiBwcm9kdWN0RGF0YSxcbiAgICAgICAgICAgIG9wZW5Qcm9kdWN0cyA6IHBlcm1pc3Npb25Hcm91cHNDdHJsLm9wZW5Qcm9kdWN0c1xuICAgICAgICB9O1xuICAgICAgICBwZXJtaXNzaW9uR3JvdXBzQ3RybC5wYWdlRGlzcGxheSA9ICdwcm9kdWN0JztcbiAgICB9O1xuXG4gICAgcGVybWlzc2lvbkdyb3Vwc0N0cmwub3Blbk5ld1Byb2R1Y3QgPSBmdW5jdGlvbihwcm9kdWN0RGF0YSl7XG4gICAgICAgIHBlcm1pc3Npb25Hcm91cHNDdHJsLm5ld1Byb2R1Y3REYXRhID0ge1xuICAgICAgICAgICAgb3BlblByb2R1Y3QgOiBwZXJtaXNzaW9uR3JvdXBzQ3RybC5vcGVuUHJvZHVjdCxcbiAgICAgICAgICAgIHByb2R1Y3REYXRhIDogcHJvZHVjdERhdGFcbiAgICAgICAgfTtcbiAgICAgICAgcGVybWlzc2lvbkdyb3Vwc0N0cmwucGFnZURpc3BsYXkgPSAnbmV3UHJvZHVjdCc7XG4gICAgfTtcblxuICAgIEJyZWFkQ3J1bWJzU2VydmljZS5icmVhZENydW1iQ2hhbmdlKDApO1xuICAgIEJyZWFkQ3J1bWJzU2VydmljZS5wdXNoKHt9LCdQRVJNSVNTSU9OX0dST1VQUycsZnVuY3Rpb24oKXtcbiAgICAgICAgcGVybWlzc2lvbkdyb3Vwc0N0cmwub3BlblByb2R1Y3RzKCk7XG4gICAgfSk7XG5cbiAgICBwZXJtaXNzaW9uR3JvdXBzQ3RybC5wcm9kdWN0c0RhdGEgPSB7XG4gICAgICAgIG9wZW5Qcm9kdWN0IDogcGVybWlzc2lvbkdyb3Vwc0N0cmwub3BlblByb2R1Y3QsXG4gICAgICAgIG9wZW5OZXdQcm9kdWN0IDogcGVybWlzc2lvbkdyb3Vwc0N0cmwub3Blbk5ld1Byb2R1Y3RcbiAgICB9O1xuICAgIHBlcm1pc3Npb25Hcm91cHNDdHJsLnBhZ2VEaXNwbGF5ID0gJ3Blcm1pc3Npb25Hcm91cHNUYWJsZSc7XG59XG5cbmFuZ3VsYXJcbiAgICAubW9kdWxlKCdURE0tRkUnKVxuICAgIC5jb250cm9sbGVyKCdwZXJtaXNzaW9uR3JvdXBzQ3RybCcgLCBwZXJtaXNzaW9uR3JvdXBzQ3RybCk7Il19