package modules

import geb.Page
import geb.Module
import geb.navigator.Navigator

class CardsHolderModule extends Module {
	static content = {
		cardTable { $("table.table.table-hover") }
		dropupBtns { $("div.btn-group.dropup.open") }
		cardName { captionText -> cardTable.find("caption", text:"$captionText") }
		cardMenu { captionText -> cardName(captionText).find("a")[0] }
		cardDDMenu(wait: true) { dropupBtns.find("a") }
		cardItemMenu {captionText -> cardName(captionText).next().find("a")[0]}
		cardItemMenuDD(wait: true) { dropupBtns.find("a")}
		cardItems {captionText -> cardName(captionText).next().find("tr") }
	}
}