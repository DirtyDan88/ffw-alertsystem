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