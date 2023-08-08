angular.module("TDM-FE").constant("AUTH_EVENTS",{loginSuccess:"auth-login-success",loginFailed:"auth-login-failed",logoutSuccess:"auth-logout-success",sessionTimeout:"auth-session-timeout",notAuthenticated:"auth-not-authenticated",notAuthorized:"auth-not-authorized"}).constant("USER_ROLES",{admin:0,owner:1,user:1}).constant("BE_BASE_URL",{url:"http://localhost:3000/api"}).constant("FE_VERSION",{version:"7.0"}).constant("TASK",{timeInterval:5e3}).constant("LOGIN_BANNER",{enabled:!1,text:"\n        This system is for the use of authorized users only.\n\n        Individuals using this computer system without authority, or in excess of their authority, are subject to having all of their activities on this system monitored and recorded by system personnel.\n\n        In the course of monitoring individuals improperly using this system, or in the course of system maintenance, the activities of authorized users may also be monitored.\n        \n        Anyone using this system expressly consents to such monitoring and is advised that if such monitoring reveals possible evidence of criminal activity, system personnel may provide the evidence of such monitoring to law enforcement officials.\n"}).constant("DASHBOARD",{display:!1});
//# sourceMappingURL=data:application/json;charset=utf8;base64,eyJ2ZXJzaW9uIjozLCJzb3VyY2VzIjpbImNvbnN0YW50cy9jb25zdGFudHMuanMiXSwibmFtZXMiOlsiYW5ndWxhciIsIm1vZHVsZSIsImNvbnN0YW50IiwibG9naW5TdWNjZXNzIiwibG9naW5GYWlsZWQiLCJsb2dvdXRTdWNjZXNzIiwic2Vzc2lvblRpbWVvdXQiLCJub3RBdXRoZW50aWNhdGVkIiwibm90QXV0aG9yaXplZCIsImFkbWluIiwib3duZXIiLCJ1c2VyIiwidXJsIiwidmVyc2lvbiIsInRpbWVJbnRlcnZhbCIsImVuYWJsZWQiLCJ0ZXh0IiwiZGlzcGxheSJdLCJtYXBwaW5ncyI6IkFBQUFBLFFBQ0tDLE9BQU8sVUFDUEMsU0FBUyxjQUFlLENBQ3JCQyxhQUFjLHFCQUNkQyxZQUFhLG9CQUNiQyxjQUFlLHNCQUNmQyxlQUFnQix1QkFDaEJDLGlCQUFrQix5QkFDbEJDLGNBQWUsd0JBRWxCTixTQUFTLGFBQWMsQ0FDcEJPLE1BQU8sRUFDUEMsTUFBTyxFQUNQQyxLQUFNLElBRVRULFNBQVMsY0FBZSxDQUNyQlUsSUFBSyw4QkFLUlYsU0FBUyxhQUFjLENBQ3BCVyxRQUFTLFFBRVpYLFNBQVMsT0FBUSxDQUNkWSxhQUFjLE1BRWpCWixTQUFTLGVBQWdCLENBQ3RCYSxTQUFTLEVBQ1RDLEtBQU0sMnNCQVVUZCxTQUFTLFlBQWEsQ0FDbkJlLFNBQVMiLCJmaWxlIjoiY29uc3RhbnRzL2NvbnN0YW50cy5qcyIsInNvdXJjZXNDb250ZW50IjpbImFuZ3VsYXJcbiAgICAubW9kdWxlKCdURE0tRkUnKVxuICAgIC5jb25zdGFudCgnQVVUSF9FVkVOVFMnLCB7XG4gICAgICAgIGxvZ2luU3VjY2VzczogJ2F1dGgtbG9naW4tc3VjY2VzcycsXG4gICAgICAgIGxvZ2luRmFpbGVkOiAnYXV0aC1sb2dpbi1mYWlsZWQnLFxuICAgICAgICBsb2dvdXRTdWNjZXNzOiAnYXV0aC1sb2dvdXQtc3VjY2VzcycsXG4gICAgICAgIHNlc3Npb25UaW1lb3V0OiAnYXV0aC1zZXNzaW9uLXRpbWVvdXQnLFxuICAgICAgICBub3RBdXRoZW50aWNhdGVkOiAnYXV0aC1ub3QtYXV0aGVudGljYXRlZCcsXG4gICAgICAgIG5vdEF1dGhvcml6ZWQ6ICdhdXRoLW5vdC1hdXRob3JpemVkJ1xuICAgIH0pXG4gICAgLmNvbnN0YW50KCdVU0VSX1JPTEVTJywge1xuICAgICAgICBhZG1pbjogMCxcbiAgICAgICAgb3duZXI6IDEsXG4gICAgICAgIHVzZXI6IDFcbiAgICB9KVxuICAgIC5jb25zdGFudCgnQkVfQkFTRV9VUkwnLCB7XG4gICAgICAgIHVybDogJ2h0dHA6Ly9sb2NhbGhvc3Q6MzAwMC9hcGknXG4gICAgICAgIC8vIHVybCA6ICdodHRwOi8vODIuODEuMTc0LjU5OjMwMDAvYXBpJ1xuICAgICAgICAvL3VybCA6ICdodHRwOi8vMTYzLjE3Mi4xNzYuMjI3OjMwMDAvYXBpJ1xuICAgICAgICAvLyAgdXJsIDogJ2h0dHA6Ly9sb2NhbGhvc3Q6MzAwMC9hcGknXG4gICAgfSlcbiAgICAuY29uc3RhbnQoJ0ZFX1ZFUlNJT04nLCB7XG4gICAgICAgIHZlcnNpb246ICc3LjAnXG4gICAgfSlcbiAgICAuY29uc3RhbnQoJ1RBU0snLCB7XG4gICAgICAgIHRpbWVJbnRlcnZhbDogNTAwMFxuICAgIH0pXG4gICAgLmNvbnN0YW50KCdMT0dJTl9CQU5ORVInLCB7XG4gICAgICAgIGVuYWJsZWQ6IGZhbHNlLFxuICAgICAgICB0ZXh0IDpgXG4gICAgICAgIFRoaXMgc3lzdGVtIGlzIGZvciB0aGUgdXNlIG9mIGF1dGhvcml6ZWQgdXNlcnMgb25seS5cblxuICAgICAgICBJbmRpdmlkdWFscyB1c2luZyB0aGlzIGNvbXB1dGVyIHN5c3RlbSB3aXRob3V0IGF1dGhvcml0eSwgb3IgaW4gZXhjZXNzIG9mIHRoZWlyIGF1dGhvcml0eSwgYXJlIHN1YmplY3QgdG8gaGF2aW5nIGFsbCBvZiB0aGVpciBhY3Rpdml0aWVzIG9uIHRoaXMgc3lzdGVtIG1vbml0b3JlZCBhbmQgcmVjb3JkZWQgYnkgc3lzdGVtIHBlcnNvbm5lbC5cblxuICAgICAgICBJbiB0aGUgY291cnNlIG9mIG1vbml0b3JpbmcgaW5kaXZpZHVhbHMgaW1wcm9wZXJseSB1c2luZyB0aGlzIHN5c3RlbSwgb3IgaW4gdGhlIGNvdXJzZSBvZiBzeXN0ZW0gbWFpbnRlbmFuY2UsIHRoZSBhY3Rpdml0aWVzIG9mIGF1dGhvcml6ZWQgdXNlcnMgbWF5IGFsc28gYmUgbW9uaXRvcmVkLlxuICAgICAgICBcbiAgICAgICAgQW55b25lIHVzaW5nIHRoaXMgc3lzdGVtIGV4cHJlc3NseSBjb25zZW50cyB0byBzdWNoIG1vbml0b3JpbmcgYW5kIGlzIGFkdmlzZWQgdGhhdCBpZiBzdWNoIG1vbml0b3JpbmcgcmV2ZWFscyBwb3NzaWJsZSBldmlkZW5jZSBvZiBjcmltaW5hbCBhY3Rpdml0eSwgc3lzdGVtIHBlcnNvbm5lbCBtYXkgcHJvdmlkZSB0aGUgZXZpZGVuY2Ugb2Ygc3VjaCBtb25pdG9yaW5nIHRvIGxhdyBlbmZvcmNlbWVudCBvZmZpY2lhbHMuXG5gXG4gICAgfSlcbiAgICAuY29uc3RhbnQoJ0RBU0hCT0FSRCcsIHtcbiAgICAgICAgZGlzcGxheTogZmFsc2VcbiAgICB9KTtcbiJdfQ==