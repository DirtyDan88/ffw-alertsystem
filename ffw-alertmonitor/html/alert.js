function setAlertKeywordAndLevel() {
    var alertKeywordShort = $("#shortKeyword").text();
    var alertLevel        = $("#alertLevel").text();
    //alert("TODO: '" + alertKeywordShort+"'");
    var keyword = "";
    if (alertKeywordShort == "F" || alertKeywordShort == "B") {
        keyword = "Brandeinsatz";
    } else if (alertKeywordShort == "H" || alertKeywordShort == "T") {
        keyword = "Technische Hilfeleistung";
    } else if (alertKeywordShort == "G") {
        keyword = "Gefahrgutunfall";
    } else if (alertKeywordShort == "W") {
        keyword = "Einsatz auf Gewässer";
    } else {
        keyword = "Unbekannt";
    }
    $("#shortKeywordDesc").text(keyword);
    
    
    var levels = new Array("Kleinbrand", "Mittelbrand", "Großbrand", 
                             "TH klein", "TH mittel", "TH groß", "Gefahrgut");
    
    $("#alertLevelDesc").text(levels[alertLevel - 1]);
}