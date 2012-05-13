/*
 * Copyright 2011 The Netty Project
 *
 * The Netty Project licenses this file to you under the Apache License,
 * version 2.0 (the "License"); you may not use this file except in compliance
 * with the License. You may obtain a copy of the License at:
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
 * License for the specific language governing permissions and limitations
 * under the License.
 */
package uk.co.bssd.netty.server.websocket;

import org.jboss.netty.buffer.ChannelBuffer;
import org.jboss.netty.buffer.ChannelBuffers;
import org.jboss.netty.util.CharsetUtil;

/**
 * Generates the demo HTML page which is served at http://localhost:8080/
 */
public final class WebSocketServerIndexPage {

    private static final String NEWLINE = "\r\n";

    public static ChannelBuffer getContent(String webSocketLocation) {
        return ChannelBuffers
                .copiedBuffer(
                        "<html><head><title>Web Socket Test</title></head>"
                                + NEWLINE
                                + "<body>"
                                + NEWLINE
                                + "<div id=\"placeholder\" style=\"width:600px;height:300px;\"></div>" + NEWLINE
                                + "<script language=\"javascript\" type=\"text/javascript\" src=\"http://people.iola.dk/olau/flot/jquery.js\"></script>"
                                + NEWLINE
                                + "<script language=\"javascript\" type=\"text/javascript\" src=\"http://people.iola.dk/olau/flot/jquery.flot.js\"></script>"
                                + NEWLINE
                                + "<script type=\"text/javascript\">"
                                + NEWLINE
                                + "var socket; var data = []; var totalPoints = 60;" + NEWLINE
                                + "var options = { lines: { show: true }, points: { show: true }, xaxis: { mode: \"time\", show: true }, yaxis: { min: 0 } };" + NEWLINE
                                + "var plot = $.plot($(\"#placeholder\"), data, options);" 
                                + NEWLINE
                                + "if (!window.WebSocket) {"
                                + NEWLINE
                                + "  window.WebSocket = window.MozWebSocket;"
                                + NEWLINE
                                + "}"
                                + NEWLINE
                                + "if (window.WebSocket) {"
                                + NEWLINE
                                + "  socket = new WebSocket(\""
                                + webSocketLocation
                                + "\");"
                                + NEWLINE
                                + " socket.onmessage = function(event) { var ta = document.getElementById('responseText'); ta.value = ta.value + '\\n' + event.data;" + NEWLINE
                                + "   if (data.length > totalPoints) { data = data.slice(1); }"
                                + "   var statistics = JSON.parse(event.data);" + NEWLINE
                                + "   var date = new Date();" + NEWLINE
                                + "   var time = (date.getTime() - (date.getTimezoneOffset() * 60 * 1000));" + NEWLINE
                                + "   if(statistics.statistics.length == 0) {" + NEWLINE
                                + "     data.push([time, 0]);" + NEWLINE
                                + "   } else {" + NEWLINE
                                + "     data.push([time, statistics.statistics[0].average]);" + NEWLINE
                                + "   };" + NEWLINE
                                + "   plot.setData([data]);" + NEWLINE
                                + "   plot.setupGrid();" + NEWLINE
                                + "   plot.draw(); " + NEWLINE
                                + " };" 
                                + NEWLINE
                                + "  socket.onopen = function(event) { var ta = document.getElementById('responseText'); ta.value = \"Web Socket opened!\"; };"
                                + NEWLINE
                                + "  socket.onclose = function(event) { var ta = document.getElementById('responseText'); ta.value = ta.value + \"Web Socket closed\"; };"
                                + NEWLINE
                                + "} else {"
                                + NEWLINE
                                + "  alert(\"Your browser does not support Web Socket.\");"
                                + NEWLINE
                                + "}"
                                + NEWLINE
                                + "</script>"
                                + NEWLINE + "<h3>Output</h3>" + NEWLINE
                                + "<textarea id=\"responseText\" style=\"width: 500px; height:300px;\"></textarea>"
                                + NEWLINE  + "</body>" + NEWLINE + "</html>" + NEWLINE,
                        CharsetUtil.US_ASCII);
    }

    private WebSocketServerIndexPage() {
        // Unused
    }
}
