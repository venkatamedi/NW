!function($){"use strict";function getControlPoints(x0,y0,x1,y1,x2,y2,tension){var d01,fa,fb,pow=Math.pow,sqrt=Math.sqrt;return[x1+(fa=tension*(d01=sqrt(pow(x1-x0,2)+pow(y1-y0,2)))/(d01+sqrt(pow(x2-x1,2)+pow(y2-y1,2))))*(x0-x2),y1+fa*(y0-y2),x1-(fb=tension-fa)*(x0-x2),y1-fb*(y0-y2)]}var line=[];function queue(ctx,type,points,cpoints){(void 0===type||"bezier"!==type&&"quadratic"!==type)&&(type="quadratic"),type+="CurveTo",0==line.length?line.push([points[0],points[1],cpoints.concat(points.slice(2)),type]):"quadraticCurveTo"==type&&2==points.length?(cpoints=cpoints.slice(0,2).concat(points),line.push([points[0],points[1],cpoints,type])):line.push([points[2],points[3],cpoints.concat(points.slice(2)),type])}function drawSpline(plot,ctx,series){if(!0===series.splines.show){var idx,x,y,cp=[],tension=series.splines.tension||.5,points=series.datapoints.points,ps=series.datapoints.pointsize,plotOffset=plot.getPlotOffset(),len=points.length,pts=[];if(line=[],len/ps<4)$.extend(series.lines,series.splines);else{for(idx=0;idx<len;idx+=ps)x=points[idx],y=points[idx+1],null==x||x<series.xaxis.min||x>series.xaxis.max||y<series.yaxis.min||y>series.yaxis.max||pts.push(series.xaxis.p2c(x)+plotOffset.left,series.yaxis.p2c(y)+plotOffset.top);for(len=pts.length,idx=0;idx<len-2;idx+=2)cp=cp.concat(getControlPoints.apply(this,pts.slice(idx,idx+6).concat([tension])));for(ctx.save(),ctx.strokeStyle=series.color,ctx.lineWidth=series.splines.lineWidth,queue(0,"quadratic",pts.slice(0,4),cp.slice(0,2)),idx=2;idx<len-3;idx+=2)queue(0,"bezier",pts.slice(idx,idx+4),cp.slice(2*idx-2,2*idx+2));queue(0,"quadratic",pts.slice(len-2,len),[cp[2*len-10],cp[2*len-9],pts[len-4],pts[len-3]]),function(points,ctx,height,fill,seriesColor){var c=$.color.parse(seriesColor);c.a="number"==typeof fill?fill:.3,c.normalize(),c=c.toString(),ctx.beginPath(),ctx.moveTo(points[0][0],points[0][1]);for(var plength=points.length,i=0;i<plength;i++)ctx[points[i][3]].apply(ctx,points[i][2]);ctx.stroke(),ctx.lineWidth=0,ctx.lineTo(points[plength-1][0],height),ctx.lineTo(points[0][0],height),ctx.closePath(),!1!==fill&&(ctx.fillStyle=c,ctx.fill())}(line,ctx,plot.height()+10,series.splines.fill,series.color),ctx.restore()}}}$.plot.plugins.push({init:function(plot){plot.hooks.drawSeries.push(drawSpline)},options:{series:{splines:{show:!1,lineWidth:2,tension:.5,fill:!1}}},name:"spline",version:"0.8.2"})}(jQuery);
//# sourceMappingURL=data:application/json;charset=utf8;base64,eyJ2ZXJzaW9uIjozLCJzb3VyY2VzIjpbInBsdWdpbnMvZmxvdC9qcXVlcnkuZmxvdC5zcGxpbmUuanMiXSwibmFtZXMiOlsiJCIsImdldENvbnRyb2xQb2ludHMiLCJ4MCIsInkwIiwieDEiLCJ5MSIsIngyIiwieTIiLCJ0ZW5zaW9uIiwiZDAxIiwiZmEiLCJmYiIsInBvdyIsIk1hdGgiLCJzcXJ0IiwibGluZSIsInF1ZXVlIiwiY3R4IiwidHlwZSIsInBvaW50cyIsImNwb2ludHMiLCJsZW5ndGgiLCJwdXNoIiwiY29uY2F0Iiwic2xpY2UiLCJkcmF3U3BsaW5lIiwicGxvdCIsInNlcmllcyIsInNwbGluZXMiLCJzaG93IiwiaWR4IiwieCIsInkiLCJjcCIsImRhdGFwb2ludHMiLCJwcyIsInBvaW50c2l6ZSIsInBsb3RPZmZzZXQiLCJnZXRQbG90T2Zmc2V0IiwibGVuIiwicHRzIiwiZXh0ZW5kIiwibGluZXMiLCJ4YXhpcyIsIm1pbiIsIm1heCIsInlheGlzIiwicDJjIiwibGVmdCIsInRvcCIsImFwcGx5IiwidGhpcyIsInNhdmUiLCJzdHJva2VTdHlsZSIsImNvbG9yIiwibGluZVdpZHRoIiwiaGVpZ2h0IiwiZmlsbCIsInNlcmllc0NvbG9yIiwiYyIsInBhcnNlIiwiYSIsIm5vcm1hbGl6ZSIsInRvU3RyaW5nIiwiYmVnaW5QYXRoIiwibW92ZVRvIiwicGxlbmd0aCIsImkiLCJzdHJva2UiLCJsaW5lVG8iLCJjbG9zZVBhdGgiLCJmaWxsU3R5bGUiLCJkcmF3TGluZSIsInJlc3RvcmUiLCJwbHVnaW5zIiwiaW5pdCIsImhvb2tzIiwiZHJhd1NlcmllcyIsIm9wdGlvbnMiLCJuYW1lIiwidmVyc2lvbiIsImpRdWVyeSJdLCJtYXBwaW5ncyI6IkNBd0NBLFNBQVVBLEdBQ04sYUFXQSxTQUFTQyxpQkFBaUJDLEdBQUlDLEdBQUlDLEdBQUlDLEdBQUlDLEdBQUlDLEdBQUlDLFNBRTlDLElBRUlDLElBQVVDLEdBQUlDLEdBRmRDLElBQU1DLEtBQUtELElBQ1hFLEtBQU9ELEtBQUtDLEtBZ0JoQixN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SUFBTSxLQS9HakgsU0FBa0JwQixPQUFRRixJQUFLdUMsT0FBUUMsS0FBTUMsYUFDekMsSUFBSUMsRUFBSTNELEVBQUVzRCxNQUFNTSxNQUFNRixhQUV0QkMsRUFBRUUsRUFBbUIsaUJBQVJKLEtBQW1CQSxLQUFPLEdBQ3ZDRSxFQUFFRyxZQUNGSCxFQUFJQSxFQUFFSSxXQUVOOUMsSUFBSStDLFlBQ0ovQyxJQUFJZ0QsT0FBTzlDLE9BQU8sR0FBRyxHQUFJQSxPQUFPLEdBQUcsSUFJbkMsSUFGQSxJQUFJK0MsUUFBVS9DLE9BQU9FLE9BRVo4QyxFQUFJLEVBQUdBLEVBQUlELFFBQVNDLElBQ3pCbEQsSUFBSUUsT0FBT2dELEdBQUcsSUFBSWpCLE1BQU1qQyxJQUFLRSxPQUFPZ0QsR0FBRyxJQUczQ2xELElBQUltRCxTQUVKbkQsSUFBSXNDLFVBQVksRUFDaEJ0QyxJQUFJb0QsT0FBT2xELE9BQU8rQyxRQUFVLEdBQUcsR0FBSVYsUUFDbkN2QyxJQUFJb0QsT0FBT2xELE9BQU8sR0FBRyxHQUFJcUMsUUFFekJ2QyxJQUFJcUQsYUFFUyxJQUFUYixPQUNBeEMsSUFBSXNELFVBQVlaLEVBQ2hCMUMsSUFBSXdDLFFBdUZSZSxDQUFTekQsS0FBTUUsSUFBS1MsS0FBSzhCLFNBQVcsR0FBSTdCLE9BQU9DLFFBQVE2QixLQUFNOUIsT0FBTzJCLE9BRXBFckMsSUFBSXdELFlBR1J6RSxFQUFFMEIsS0FBS2dELFFBQVFwRCxLQUFLLENBQ2hCcUQsS0FBTSxTQUFTakQsTUFDWEEsS0FBS2tELE1BQU1DLFdBQVd2RCxLQUFLRyxhQUUvQnFELFFBQVMsQ0FDTG5ELE9BQVEsQ0FDSkMsUUFBUyxDQUNMQyxNQUFNLEVBQ04wQixVQUFXLEVBQ1gvQyxRQUFTLEdBQ1RpRCxNQUFNLEtBSWxCc0IsS0FBTSxTQUNOQyxRQUFTLFVBektqQixDQTJLR0MiLCJmaWxlIjoicGx1Z2lucy9mbG90L2pxdWVyeS5mbG90LnNwbGluZS5qcyIsInNvdXJjZXNDb250ZW50IjpbIi8qKlxuICogRmxvdCBwbHVnaW4gdGhhdCBwcm92aWRlcyBzcGxpbmUgaW50ZXJwb2xhdGlvbiBmb3IgbGluZSBncmFwaHNcbiAqIGF1dGhvcjogQWxleCBCYXJkYXMgPCBhbGV4LmJhcmRhc0BnbWFpbC5jb20gPlxuICogbW9kaWZpZWQgYnk6IEF2aSBLb2huIGh0dHBzOi8vZ2l0aHViLmNvbS9BTUtvaG5cbiAqIGJhc2VkIG9uIHRoZSBzcGxpbmUgaW50ZXJwb2xhdGlvbiBkZXNjcmliZWQgYXQ6XG4gKlx0XHQgaHR0cDovL3NjYWxlZGlubm92YXRpb24uY29tL2FuYWx5dGljcy9zcGxpbmVzL2Fib3V0U3BsaW5lcy5odG1sXG4gKlxuICogRXhhbXBsZSB1c2FnZTogKGFkZCBpbiBwbG90IG9wdGlvbnMgc2VyaWVzIG9iamVjdClcbiAqXHRcdGZvciBsaW5lc3BsaW5lOlxuICpcdFx0XHRzZXJpZXM6IHtcbiAqXHRcdFx0XHQuLi5cbiAqXHRcdFx0XHRsaW5lczoge1xuICpcdFx0XHRcdFx0c2hvdzogZmFsc2VcbiAqXHRcdFx0XHR9LFxuICpcdFx0XHRcdHNwbGluZXM6IHtcbiAqXHRcdFx0XHRcdHNob3c6IHRydWUsXG4gKlx0XHRcdFx0XHR0ZW5zaW9uOiB4LCAoZmxvYXQgYmV0d2VlbiAwIGFuZCAxLCBkZWZhdWx0cyB0byAwLjUpLFxuICpcdFx0XHRcdFx0bGluZVdpZHRoOiB5IChudW1iZXIsIGRlZmF1bHRzIHRvIDIpLFxuICpcdFx0XHRcdFx0ZmlsbDogeiAoZmxvYXQgYmV0d2VlbiAwIC4uIDEgb3IgZmFsc2UsIGFzIGluIGZsb3QgZG9jdW1lbnRhdGlvbilcbiAqXHRcdFx0XHR9LFxuICpcdFx0XHRcdC4uLlxuICpcdFx0XHR9XG4gKlx0XHRhcmVhc3BsaW5lOlxuICpcdFx0XHRzZXJpZXM6IHtcbiAqXHRcdFx0XHQuLi5cbiAqXHRcdFx0XHRsaW5lczoge1xuICpcdFx0XHRcdFx0c2hvdzogdHJ1ZSxcbiAqXHRcdFx0XHRcdGxpbmVXaWR0aDogMCwgKGxpbmUgZHJhd2luZyB3aWxsIG5vdCBleGVjdXRlKVxuICpcdFx0XHRcdFx0ZmlsbDogeCwgKGZsb2F0IGJldHdlZW4gMCAuLiAxLCBhcyBpbiBmbG90IGRvY3VtZW50YXRpb24pXG4gKlx0XHRcdFx0XHQuLi5cbiAqXHRcdFx0XHR9LFxuICpcdFx0XHRcdHNwbGluZXM6IHtcbiAqXHRcdFx0XHRcdHNob3c6IHRydWUsXG4gKlx0XHRcdFx0XHR0ZW5zaW9uOiAwLjUgKGZsb2F0IGJldHdlZW4gMCBhbmQgMSlcbiAqXHRcdFx0XHR9LFxuICpcdFx0XHRcdC4uLlxuICpcdFx0XHR9XG4gKlxuICovXG5cbihmdW5jdGlvbigkKSB7XG4gICAgJ3VzZSBzdHJpY3QnXG5cbiAgICAvKipcbiAgICAgKiBAcGFyYW0ge051bWJlcn0geDAsIHkwLCB4MSwgeTE6IGNvb3JkaW5hdGVzIG9mIHRoZSBlbmQgKGtub3QpIHBvaW50cyBvZiB0aGUgc2VnbWVudFxuICAgICAqIEBwYXJhbSB7TnVtYmVyfSB4MiwgeTI6IHRoZSBuZXh0IGtub3QgKG5vdCBjb25uZWN0ZWQsIGJ1dCBuZWVkZWQgdG8gY2FsY3VsYXRlIHAyKVxuICAgICAqIEBwYXJhbSB7TnVtYmVyfSB0ZW5zaW9uOiBjb250cm9sIGhvdyBmYXIgdGhlIGNvbnRyb2wgcG9pbnRzIHNwcmVhZFxuICAgICAqIEByZXR1cm4ge0FycmF5fTogcDEgLT4gY29udHJvbCBwb2ludCwgZnJvbSB4MSBiYWNrIHRvd2FyZCB4MFxuICAgICAqIFx0XHRcdFx0XHRwMiAtPiB0aGUgbmV4dCBjb250cm9sIHBvaW50LCByZXR1cm5lZCB0byBiZWNvbWUgdGhlIG5leHQgc2VnbWVudCdzIHAxXG4gICAgICpcbiAgICAgKiBAYXBpIHByaXZhdGVcbiAgICAgKi9cbiAgICBmdW5jdGlvbiBnZXRDb250cm9sUG9pbnRzKHgwLCB5MCwgeDEsIHkxLCB4MiwgeTIsIHRlbnNpb24pIHtcblxuICAgICAgICB2YXIgcG93ID0gTWF0aC5wb3csXG4gICAgICAgICAgICBzcXJ0ID0gTWF0aC5zcXJ0LFxuICAgICAgICAgICAgZDAxLCBkMTIsIGZhLCBmYiwgcDF4LCBwMXksIHAyeCwgcDJ5O1xuXG4gICAgICAgIC8vICBTY2FsaW5nIGZhY3RvcnM6IGRpc3RhbmNlcyBmcm9tIHRoaXMga25vdCB0byB0aGUgcHJldmlvdXMgYW5kIGZvbGxvd2luZyBrbm90cy5cbiAgICAgICAgZDAxID0gc3FydChwb3coeDEgLSB4MCwgMikgKyBwb3coeTEgLSB5MCwgMikpO1xuICAgICAgICBkMTIgPSBzcXJ0KHBvdyh4MiAtIHgxLCAyKSArIHBvdyh5MiAtIHkxLCAyKSk7XG5cbiAgICAgICAgZmEgPSB0ZW5zaW9uICogZDAxIC8gKGQwMSArIGQxMik7XG4gICAgICAgIGZiID0gdGVuc2lvbiAtIGZhO1xuXG4gICAgICAgIHAxeCA9IHgxICsgZmEgKiAoeDAgLSB4Mik7XG4gICAgICAgIHAxeSA9IHkxICsgZmEgKiAoeTAgLSB5Mik7XG5cbiAgICAgICAgcDJ4ID0geDEgLSBmYiAqICh4MCAtIHgyKTtcbiAgICAgICAgcDJ5ID0geTEgLSBmYiAqICh5MCAtIHkyKTtcblxuICAgICAgICByZXR1cm4gW3AxeCwgcDF5LCBwMngsIHAyeV07XG4gICAgfVxuXG4gICAgdmFyIGxpbmUgPSBbXTtcblxuICAgIGZ1bmN0aW9uIGRyYXdMaW5lKHBvaW50cywgY3R4LCBoZWlnaHQsIGZpbGwsIHNlcmllc0NvbG9yKSB7XG4gICAgICAgIHZhciBjID0gJC5jb2xvci5wYXJzZShzZXJpZXNDb2xvcik7XG5cbiAgICAgICAgYy5hID0gdHlwZW9mIGZpbGwgPT0gXCJudW1iZXJcIiA/IGZpbGwgOiAuMztcbiAgICAgICAgYy5ub3JtYWxpemUoKTtcbiAgICAgICAgYyA9IGMudG9TdHJpbmcoKTtcblxuICAgICAgICBjdHguYmVnaW5QYXRoKCk7XG4gICAgICAgIGN0eC5tb3ZlVG8ocG9pbnRzWzBdWzBdLCBwb2ludHNbMF1bMV0pO1xuXG4gICAgICAgIHZhciBwbGVuZ3RoID0gcG9pbnRzLmxlbmd0aDtcblxuICAgICAgICBmb3IgKHZhciBpID0gMDsgaSA8IHBsZW5ndGg7IGkrKykge1xuICAgICAgICAgICAgY3R4W3BvaW50c1tpXVszXV0uYXBwbHkoY3R4LCBwb2ludHNbaV1bMl0pO1xuICAgICAgICB9XG5cbiAgICAgICAgY3R4LnN0cm9rZSgpO1xuXG4gICAgICAgIGN0eC5saW5lV2lkdGggPSAwO1xuICAgICAgICBjdHgubGluZVRvKHBvaW50c1twbGVuZ3RoIC0gMV1bMF0sIGhlaWdodCk7XG4gICAgICAgIGN0eC5saW5lVG8ocG9pbnRzWzBdWzBdLCBoZWlnaHQpO1xuXG4gICAgICAgIGN0eC5jbG9zZVBhdGgoKTtcblxuICAgICAgICBpZiAoZmlsbCAhPT0gZmFsc2UpIHtcbiAgICAgICAgICAgIGN0eC5maWxsU3R5bGUgPSBjO1xuICAgICAgICAgICAgY3R4LmZpbGwoKTtcbiAgICAgICAgfVxuICAgIH1cblxuICAgIC8qKlxuICAgICAqIEBwYXJhbSB7T2JqZWN0fSBjdHg6IGNhbnZhcyBjb250ZXh0XG4gICAgICogQHBhcmFtIHtTdHJpbmd9IHR5cGU6IGFjY2VwdGVkIHN0cmluZ3M6ICdiZXppZXInIG9yICdxdWFkcmF0aWMnIChkZWZhdWx0cyB0byBxdWFkcmF0aWMpXG4gICAgICogQHBhcmFtIHtBcnJheX0gcG9pbnRzOiAyIHBvaW50cyBmb3Igd2hpY2ggdG8gZHJhdyB0aGUgaW50ZXJwb2xhdGlvblxuICAgICAqIEBwYXJhbSB7QXJyYXl9IGNwb2ludHM6IGNvbnRyb2wgcG9pbnRzIGZvciB0aG9zZSBzZWdtZW50IHBvaW50c1xuICAgICAqXG4gICAgICogQGFwaSBwcml2YXRlXG4gICAgICovXG4gICAgZnVuY3Rpb24gcXVldWUoY3R4LCB0eXBlLCBwb2ludHMsIGNwb2ludHMpIHtcbiAgICAgICAgaWYgKHR5cGUgPT09IHZvaWQgMCB8fCAodHlwZSAhPT0gJ2JlemllcicgJiYgdHlwZSAhPT0gJ3F1YWRyYXRpYycpKSB7XG4gICAgICAgICAgICB0eXBlID0gJ3F1YWRyYXRpYyc7XG4gICAgICAgIH1cbiAgICAgICAgdHlwZSA9IHR5cGUgKyAnQ3VydmVUbyc7XG5cbiAgICAgICAgaWYgKGxpbmUubGVuZ3RoID09IDApIGxpbmUucHVzaChbcG9pbnRzWzBdLCBwb2ludHNbMV0sIGNwb2ludHMuY29uY2F0KHBvaW50cy5zbGljZSgyKSksIHR5cGVdKTtcbiAgICAgICAgZWxzZSBpZiAodHlwZSA9PSBcInF1YWRyYXRpY0N1cnZlVG9cIiAmJiBwb2ludHMubGVuZ3RoID09IDIpIHtcbiAgICAgICAgICAgIGNwb2ludHMgPSBjcG9pbnRzLnNsaWNlKDAsIDIpLmNvbmNhdChwb2ludHMpO1xuXG4gICAgICAgICAgICBsaW5lLnB1c2goW3BvaW50c1swXSwgcG9pbnRzWzFdLCBjcG9pbnRzLCB0eXBlXSk7XG4gICAgICAgIH1cbiAgICAgICAgZWxzZSBsaW5lLnB1c2goW3BvaW50c1syXSwgcG9pbnRzWzNdLCBjcG9pbnRzLmNvbmNhdChwb2ludHMuc2xpY2UoMikpLCB0eXBlXSk7XG4gICAgfVxuXG4gICAgLyoqXG4gICAgICogQHBhcmFtIHtPYmplY3R9IHBsb3RcbiAgICAgKiBAcGFyYW0ge09iamVjdH0gY3R4OiBjYW52YXMgY29udGV4dFxuICAgICAqIEBwYXJhbSB7T2JqZWN0fSBzZXJpZXNcbiAgICAgKlxuICAgICAqIEBhcGkgcHJpdmF0ZVxuICAgICAqL1xuXG4gICAgZnVuY3Rpb24gZHJhd1NwbGluZShwbG90LCBjdHgsIHNlcmllcykge1xuICAgICAgICAvLyBOb3QgaW50ZXJlc3RlZCBpZiBzcGxpbmUgaXMgbm90IHJlcXVlc3RlZFxuICAgICAgICBpZiAoc2VyaWVzLnNwbGluZXMuc2hvdyAhPT0gdHJ1ZSkge1xuICAgICAgICAgICAgcmV0dXJuO1xuICAgICAgICB9XG5cbiAgICAgICAgdmFyIGNwID0gW10sXG4gICAgICAgIC8vIGFycmF5IG9mIGNvbnRyb2wgcG9pbnRzXG4gICAgICAgICAgICB0ZW5zaW9uID0gc2VyaWVzLnNwbGluZXMudGVuc2lvbiB8fCAwLjUsXG4gICAgICAgICAgICBpZHgsIHgsIHksIHBvaW50cyA9IHNlcmllcy5kYXRhcG9pbnRzLnBvaW50cyxcbiAgICAgICAgICAgIHBzID0gc2VyaWVzLmRhdGFwb2ludHMucG9pbnRzaXplLFxuICAgICAgICAgICAgcGxvdE9mZnNldCA9IHBsb3QuZ2V0UGxvdE9mZnNldCgpLFxuICAgICAgICAgICAgbGVuID0gcG9pbnRzLmxlbmd0aCxcbiAgICAgICAgICAgIHB0cyA9IFtdO1xuXG4gICAgICAgIGxpbmUgPSBbXTtcblxuICAgICAgICAvLyBDYW5ub3QgZGlzcGxheSBhIGxpbmVzcGxpbmUvYXJlYXNwbGluZSBpZiB0aGVyZSBhcmUgbGVzcyB0aGFuIDMgcG9pbnRzXG4gICAgICAgIGlmIChsZW4gLyBwcyA8IDQpIHtcbiAgICAgICAgICAgICQuZXh0ZW5kKHNlcmllcy5saW5lcywgc2VyaWVzLnNwbGluZXMpO1xuICAgICAgICAgICAgcmV0dXJuO1xuICAgICAgICB9XG5cbiAgICAgICAgZm9yIChpZHggPSAwOyBpZHggPCBsZW47IGlkeCArPSBwcykge1xuICAgICAgICAgICAgeCA9IHBvaW50c1tpZHhdO1xuICAgICAgICAgICAgeSA9IHBvaW50c1tpZHggKyAxXTtcbiAgICAgICAgICAgIGlmICh4ID09IG51bGwgfHwgeCA8IHNlcmllcy54YXhpcy5taW4gfHwgeCA+IHNlcmllcy54YXhpcy5tYXggfHwgeSA8IHNlcmllcy55YXhpcy5taW4gfHwgeSA+IHNlcmllcy55YXhpcy5tYXgpIHtcbiAgICAgICAgICAgICAgICBjb250aW51ZTtcbiAgICAgICAgICAgIH1cblxuICAgICAgICAgICAgcHRzLnB1c2goc2VyaWVzLnhheGlzLnAyYyh4KSArIHBsb3RPZmZzZXQubGVmdCwgc2VyaWVzLnlheGlzLnAyYyh5KSArIHBsb3RPZmZzZXQudG9wKTtcbiAgICAgICAgfVxuXG4gICAgICAgIGxlbiA9IHB0cy5sZW5ndGg7XG5cbiAgICAgICAgLy8gRHJhdyBhbiBvcGVuIGN1cnZlLCBub3QgY29ubmVjdGVkIGF0IHRoZSBlbmRzXG4gICAgICAgIGZvciAoaWR4ID0gMDsgaWR4IDwgbGVuIC0gMjsgaWR4ICs9IDIpIHtcbiAgICAgICAgICAgIGNwID0gY3AuY29uY2F0KGdldENvbnRyb2xQb2ludHMuYXBwbHkodGhpcywgcHRzLnNsaWNlKGlkeCwgaWR4ICsgNikuY29uY2F0KFt0ZW5zaW9uXSkpKTtcbiAgICAgICAgfVxuXG4gICAgICAgIGN0eC5zYXZlKCk7XG4gICAgICAgIGN0eC5zdHJva2VTdHlsZSA9IHNlcmllcy5jb2xvcjtcbiAgICAgICAgY3R4LmxpbmVXaWR0aCA9IHNlcmllcy5zcGxpbmVzLmxpbmVXaWR0aDtcblxuICAgICAgICBxdWV1ZShjdHgsICdxdWFkcmF0aWMnLCBwdHMuc2xpY2UoMCwgNCksIGNwLnNsaWNlKDAsIDIpKTtcblxuICAgICAgICBmb3IgKGlkeCA9IDI7IGlkeCA8IGxlbiAtIDM7IGlkeCArPSAyKSB7XG4gICAgICAgICAgICBxdWV1ZShjdHgsICdiZXppZXInLCBwdHMuc2xpY2UoaWR4LCBpZHggKyA0KSwgY3Auc2xpY2UoMiAqIGlkeCAtIDIsIDIgKiBpZHggKyAyKSk7XG4gICAgICAgIH1cblxuICAgICAgICBxdWV1ZShjdHgsICdxdWFkcmF0aWMnLCBwdHMuc2xpY2UobGVuIC0gMiwgbGVuKSwgW2NwWzIgKiBsZW4gLSAxMF0sIGNwWzIgKiBsZW4gLSA5XSwgcHRzW2xlbiAtIDRdLCBwdHNbbGVuIC0gM11dKTtcblxuICAgICAgICBkcmF3TGluZShsaW5lLCBjdHgsIHBsb3QuaGVpZ2h0KCkgKyAxMCwgc2VyaWVzLnNwbGluZXMuZmlsbCwgc2VyaWVzLmNvbG9yKTtcblxuICAgICAgICBjdHgucmVzdG9yZSgpO1xuICAgIH1cblxuICAgICQucGxvdC5wbHVnaW5zLnB1c2goe1xuICAgICAgICBpbml0OiBmdW5jdGlvbihwbG90KSB7XG4gICAgICAgICAgICBwbG90Lmhvb2tzLmRyYXdTZXJpZXMucHVzaChkcmF3U3BsaW5lKTtcbiAgICAgICAgfSxcbiAgICAgICAgb3B0aW9uczoge1xuICAgICAgICAgICAgc2VyaWVzOiB7XG4gICAgICAgICAgICAgICAgc3BsaW5lczoge1xuICAgICAgICAgICAgICAgICAgICBzaG93OiBmYWxzZSxcbiAgICAgICAgICAgICAgICAgICAgbGluZVdpZHRoOiAyLFxuICAgICAgICAgICAgICAgICAgICB0ZW5zaW9uOiAwLjUsXG4gICAgICAgICAgICAgICAgICAgIGZpbGw6IGZhbHNlXG4gICAgICAgICAgICAgICAgfVxuICAgICAgICAgICAgfVxuICAgICAgICB9LFxuICAgICAgICBuYW1lOiAnc3BsaW5lJyxcbiAgICAgICAgdmVyc2lvbjogJzAuOC4yJ1xuICAgIH0pO1xufSkoalF1ZXJ5KTtcbiJdfQ==