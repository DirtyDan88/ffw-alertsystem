/*
    Copyright (c) 2015, Max Stark <max.stark88@web.de> 
        All rights reserved.
    
    This file is part of ffw-alertsystem, which is free software: you 
    can redistribute it and/or modify it under the terms of the GNU 
    General Public License as published by the Free Software Foundation, 
    either version 2 of the License, or (at your option) any later 
    version.
    
    This program is distributed in the hope that it will be useful,
    but WITHOUT ANY WARRANTY; without even the implied warranty of
    MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU
    General Public License for more details. 
    
    You should have received a copy of the GNU General Public License 
    along with this program; if not, see <http://www.gnu.org/licenses/>.
*/

function setAlertKeywordAndLevel() {
    var shortKeyword = $("#shortKeyword").text();
        shortKeyword = $.trim(shortKeyword);
    var alertLevel   = $("#alertLevel").text();
    var keyword      = "";
    
    if (shortKeyword == "F" || shortKeyword == "B") {
        keyword = "Brandeinsatz";
    } else if (shortKeyword == "H" || shortKeyword == "T") {
        keyword = "Technische Hilfeleistung";
    } else if (shortKeyword == "G") {
        keyword = "Gefahrgutunfall";
    } else if (shortKeyword == "W") {
        keyword = "Einsatz auf Gewässer";
    } else {
        keyword = "Unbekannt";
    }
    $("#shortKeywordDesc").text(keyword);
    
    var levels = new Array("Kleinbrand", "Mittelbrand", "Großbrand", 
                             "TH klein", "TH mittel", "TH groß", "Gefahrgut");
    $("#alertLevelDesc").text(levels[alertLevel - 1]);
    
    // TODO: get some more pictures
    $("#keywordImage").prepend('<img src="../images/F.png" />')
}