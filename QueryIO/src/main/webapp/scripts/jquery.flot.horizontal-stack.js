/**
Flot plugin for stacking data sets, i.e. putting them on top of each
other, for accumulative graphs. Note that the plugin assumes the data
is sorted on x. Also note that stacking a mix of positive and negative
values in most instances doesn't make sense (so it looks weird).

Two or more series are stacked when their "stack" attribute is set to
the same key (which can be any number or string or just "true"). To
specify the default stack, you can set

        series: {
                stack: null or true or key (number/string)
        }

or specify it for a specific series

        $.plot($("#placeholder"), [{ data: [ ... ], stack: true ])

The stacking order is determined by the order of the data series in
the array (later series end up on top of the previous).

Internally, the plugin modifies the datapoints in each series, adding
an offset to the y value. For line series, extra data points are
inserted through interpolation. For bar charts, the second y value is
also adjusted.
*/

(function ($) {
        var options = {
                series: { stack: null } // or number/string
        };

        function init(plot) {

                // will be built up dynamically as a hash from x-value, or y-value if horizontal
                var stackBases = {};

                function stackData(plot, s, datapoints) {
                        if (!s.stack)
                                return;

                        var newPoints = [];
                        
                        if (s.bars.horizontal)
                                for (var i=0; i <  datapoints.points.length; i += 3) {
                                        // note that the values need to be turned into absolute x-values.
                                        // in other words, if you were to stack (x1, y), (x2, y), and (x3, y),
                                        // (each from different series, which is where stackBases comes in),
                                        // you'd want the new points to be (x1, y, 0), (x1+x2, y, x1), (x1+x2+x3, y, x1+x2)
                                        // generally, (thisValue + (base up to this point), y, + (base up to this point))
                                        if (!stackBases[datapoints.points[i+1]]) {
                                                stackBases[datapoints.points[i+1]] = 0;
                                        }
                                        newPoints[i] = datapoints.points[i] + stackBases[datapoints.points[i+1]];
                                        newPoints[i+1] = datapoints.points[i+1];
                                        newPoints[i+2] = stackBases[datapoints.points[i+1]];
                                        stackBases[datapoints.points[i+1]] += datapoints.points[i];
                                }
                        else
                                for (var i=0; i <  datapoints.points.length; i += 3) {
                                        // note that the values need to be turned into absolute y-values.
                                        // in other words, if you were to stack (x, y1), (x, y2), and (x, y3),
                                        // (each from different series, which is where stackBases comes in),
                                        // you'd want the new points to be (x, y1, 0), (x, y1+y2, y1), (x, y1+y2+y3, y1+y2)
                                        // generally, (x, thisValue + (base up to this point), + (base up to this point))
                                        if (!stackBases[datapoints.points[i]]) {
                                                stackBases[datapoints.points[i]] = 0;
                                        }
                                        newPoints[i] = datapoints.points[i];
                                        newPoints[i+1] = datapoints.points[i+1] + stackBases[datapoints.points[i]];
                                        newPoints[i+2] = stackBases[datapoints.points[i]];
                                        stackBases[datapoints.points[i]] += datapoints.points[i+1];
                                }

                        datapoints.points = newPoints;
                }

                plot.hooks.processDatapoints.push(stackData);
        }

        $.plot.plugins.push({
                init: init,
                options: options,
                name: 'stack',
                version: '1.1'
        });
})(jQuery);