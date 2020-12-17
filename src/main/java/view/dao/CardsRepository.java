package view.dao;

import view.interfaces.OnClickListener;
import view.interfaces.ViewManipulations;
import view.model.Card;

import java.util.ArrayList;
import java.util.List;

public class CardsRepository {
    private final List<Card> cardsList = new ArrayList<>();
    private final ViewManipulations cardsManipulator;
    private final OnClickListener listener;
    public CardsRepository(OnClickListener listener, ViewManipulations cardsManipulator){
        this.cardsManipulator = cardsManipulator;
        this.listener = listener;
    }
    public List<Card> getAll(){
        return cardsList;
    }
    public void add(Card card,int index){
        card.setOnClickListener(listener);
        cardsList.add(card);
        cardsManipulator.add(card.getView(),index,0);
    }
    public void remove(int index){
        cardsManipulator.remove(cardsList.get(index).getView());
        cardsList.remove(index);
    }
    public void remove(Card card){
        cardsManipulator.remove(card.getView());
        cardsList.remove(card);
    }

    public int getSize(){
        return cardsList.size();
    }

}
