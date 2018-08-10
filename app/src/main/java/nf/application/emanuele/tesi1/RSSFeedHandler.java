package nf.application.emanuele.tesi1;

import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;

public class RSSFeedHandler extends DefaultHandler {
    private RSSFeed feed;
    private RSSItem item;

    private boolean feedTitleHasBeenRead = false;
    private boolean imageTitleHasBeenRead = false;

    private boolean imageRead = false;
    private boolean tramaBreveRead = false;
    private boolean registaRead = false;
    private boolean proiezioniFinished = false;
    private boolean lastProiezione = false;
    private boolean attoriFinished = false;
    private boolean genereRead = false;
    private boolean annoRead = false;
    private boolean recensioneRead = false;
    private boolean criticaRead = false;

    private boolean isTitle = false;
    private boolean isB = false;
    private boolean isLink = false;
    private boolean isP = false;
    private boolean isBr = false;
    public boolean isDescription = false;

    public RSSFeed getFeed() {
        return feed;
    }

    public void startDocument() {
        feed = new RSSFeed();
        item = new RSSItem();
    }

    public void startElement(String namespaceURI, String localName,
                             String qName, Attributes atts)  {

        if (qName.equals("item")) {
            item = new RSSItem();
            imageRead = false;
            tramaBreveRead = false;
            registaRead = false;
            proiezioniFinished = false;
            lastProiezione = false;
            attoriFinished = false;
            genereRead = false;
            annoRead = false;
            recensioneRead = false;
            criticaRead = false;
            return;
        } else if (qName.equals("title")) {
            isTitle = true;
            return;
        } else if (qName.contains("img src")) {
            isLink = true;
            return;
        } else if (qName.equals("b")) {
            isB = true;
            return;
        } else if (qName.equals("br")) {
            isBr = true;
            return;
        }else if (qName.equals("p")){
            isP = true;
            return;
        }else if (qName.equals("description")){
        isDescription = true;
        return;
    }

    }

    public void endElement(String namespaceURI, String localName, String qName) throws SAXException {
        if (qName.equals("item")) {
            feed.addItem(item);
            return;
        }
        if (qName.equals("title")){
            return;
        }
    }

    public void characters(char ch[], int start, int length) {
        String s = new String(ch, start, length);
        if (isTitle) {
            if (feedTitleHasBeenRead == false) {
                feed.setTitle(s);
                feedTitleHasBeenRead = true;
            } else if (imageTitleHasBeenRead == false) {
                imageTitleHasBeenRead = true;
            }else{
                item.setTitolo(s);
            }
            isTitle = false;
        } else if (isLink) {
            if (imageRead == false) {
                //prendo l'immagine
                imageRead = true;
            } else if (registaRead == false) {
                item.setRegista(s);
                registaRead = true;
            } else if (attoriFinished == false) {
                item.addAttori(s);
            }else if (genereRead == false){
                item.setGenere(s);
                genereRead = true;
            }else if (annoRead == false){
                item.setAnno(s);
                annoRead = true;
            } else if (tramaBreveRead == true && proiezioniFinished == false){
                //salva in String cinema globale il cinema e tira su orari e manda insieme, non sono capace
                item.addFilm(s,"");
                lastProiezione=true;
            } else if (recensioneRead == false){
                recensioneRead=true;
            }else if (criticaRead == false){
                criticaRead = true;
            }else if (criticaRead == true){
                //aggiungi il trailer, non sono capace
            }
            isLink = false;
        }else if (isBr){
            if (registaRead==true){
                attoriFinished = true;
            }
            isBr = false;
        } else if (isP) {
            item.setTrama(s);
            isP = false;
        } else if (isB) {
            if (tramaBreveRead == false){
                item.setTramaBreve(s);
                tramaBreveRead = true;
            } else if (lastProiezione == true){
                proiezioniFinished = true;
            }
            isB = false;
        }else if (isDescription){
            isDescription = false;
            return;
        }
    }
}