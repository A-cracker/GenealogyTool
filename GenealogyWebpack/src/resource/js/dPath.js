var dPath = {
	init : function(data){
		var options = arguments.length <= 1 || arguments[1] === undefined ? {} : arguments[1];
		var opts = _.defaultsDeep(options || {}, {
			foreignObject : {
				width : 100,
				height : 25
			},
			svg : {
				width : 500,
				height : 500
			},
			step : {
				x:10,
				y:3,
			},
			target: '#graph',
			callbacks: {
				nodeClick: function(id) {
					console.log(id);
				}
			}
		});
		var compute = {
			computeForeignObjectX :function(x,y){
				return x-(opts.foreignObject.width)/2;
			},

			computeForeignObjectY :function(y){
				return y-(opts.foreignObject.height)/2;
			},

			computeSourceLineXY:function(x,y){
				return {
					x:x+(opts.step.x)/2,
					y:y
				};
			},

			computeTargetLineXY: function(x,y){
				return {
					x:x-(opts.step.x)/2,
					y:y
				};
			}
		};
		//计算起始foreignObject中心点的坐标
		opts.start = {
			x : opts.foreignObject.width,
		 	y : opts.svg.height/2
		};
		var zoom = d3.zoom()
					 .scaleExtent([0.1, 10])
					 .on('zoom', function () {
                  		svg.attr('transform', d3.event.transform.translate(0, 0));
                	 });
        var svg = d3.select(opts.target)
        			.append('svg')
        			.attr('width', opts.svg.width)
        			.attr('height', 500)
        			.call(zoom)
        			.append('g')
        			.attr('transform', 'translate(' + 0 + ',' + 0 + ')');
        var lineData = [];
        var nodeData = [];
        var depth = 0;
        //compute the nodeData
        var length = data.length;
        for(var i=0;i<length;i++){
            if(i==0){
                nodeData.push(
                	{
                		"x":opts.start.x,
						"y":opts.start.y,
						"id":data[i]['sourceId'],
						"name":data[i]['source'],
						"gender":data[i]['sourceClass']
                	}
                );
            }
            //source是target的父亲或母亲
            if(data[i]['relation']==1){
                nodeData.push(
                	{
                		"x": opts.start.x + ((i+1)*2) * opts.foreignObject.width,
						"y": opts.start.y + (++depth) * opts.foreignObject.height*2,
						"id":data[i]['targetId'],
						"name":data[i]['target'],
						"gender":data[i]['targetClass']
                	}
                );
            }
            //source是target的父亲或母亲
            else if(data[i]['relation']==2){
                nodeData.push(
                	{
                		"x": opts.start.x + ((i+1)*2) * opts.foreignObject.width,
						"y": opts.start.y + (--depth) * opts.foreignObject.height*2,
						"id":data[i]['targetId'],
						"name":data[i]['target'],
						"gender":data[i]['targetClass']
                	}
                );
            }
            //source是target的配偶
            else{
                nodeData.push(
                	{
                		"x": opts.start.x + ((i+1)*2) * opts.foreignObject.width,
						"y": opts.start.y + (depth) * opts.foreignObject.height*2,
						"id":data[i]['targetId'],
						"name":data[i]['target'],
						"gender":data[i]['targetClass']
                	}
                );
            }
        }

        //compute the lineData
        length = nodeData.length - 1;
        for(var i=0;i<length;i++){
            var temp = [];
            var point = {
            	source : compute.computeSourceLineXY(nodeData[i].x,nodeData[i].y),
            	target : compute.computeTargetLineXY(nodeData[i+1].x,nodeData[i+1].y)
            }
            temp.push(
            	{
            		"x":point.source.x,
            		"y":point.source.y,
            	}
            );
            temp.push(
            	{
            		"x":point.source.x,
            		"y":(point.source.y + point.target.y)/2,
            	}
            );
            temp.push(
            	{
            		"x":point.target.x,
            		"y":(point.source.y + point.target.y)/2,
            	}
            );
            temp.push(
            	{
            		"x":point.target.x,
            		"y":point.target.y,
            	}
            );
            lineData.push(temp);
        }

        var line = d3.line()
        			 .x(function(d) { return d.x; })
        			 .y(function(d) { return d.y; });
        var paths = svg.selectAll("path")
                .data(lineData)
                .enter()
                .append("path")
                .attr("d",function(data){return line(data)})
                .attr("class", "linage");
        var nodes = svg.selectAll('.node')
        		.data(nodeData)
        		.enter()
        		.append('foreignObject')
        		.attr('x', function (d) {
		        	return compute.computeForeignObjectX(d.x);
		        }).attr('y', function (d) {
		          	return compute.computeForeignObjectY(d.y);
		        }).attr('width',function(d){
		            return opts.foreignObject.width;
		        }).attr('height',function(d){
		            return opts.foreignObject.height;
		        }).html(function (d) {
		          	var node = '';
		            node += '<div ';
		            node += 'style="height:100%;width:100%;" ';
		            node += 'class="' + d.gender + '">\n';
		            node += '<p ';
		            node += 'align="center" ';
		            node += 'class="' + "nodeText" + '">\n';
		            node += d.name;
		            node += '</p>\n';
		            node += '</div>';
		          return node;
		        }).on('click', function (d) {
	                opts.callbacks.nodeClick(d.id);
	            });
        if(length == 1){
        	if(data[0]['relation'] == 4){
	        	var text = svg.append("text")
	        				  .attr('x', opts.start.x+opts.foreignObject.width/2+15)
	        				  .attr('y', opts.start.y-5)
	        				  .text('无亲友路径');
            }else if(data[0]['relation'] == 5){
            	var text = svg.append("text")
							  .attr('x', opts.start.x+opts.foreignObject.width-15)
							  .attr('y', opts.start.y-5)
							  .text('自己');
            }
        }
        d3.select("#depth").text(""+Math.abs(depth));
	}
}