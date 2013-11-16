package com.oligon.emergency;

import android.content.Context;

import co.juliansuarez.libwizardpager.wizard.model.AbstractWizardModel;
import co.juliansuarez.libwizardpager.wizard.model.BranchPage;
import co.juliansuarez.libwizardpager.wizard.model.ContentPage;
import co.juliansuarez.libwizardpager.wizard.model.PageList;
import co.juliansuarez.libwizardpager.wizard.model.SingleFixedChoicePage;

public class WizardModel extends AbstractWizardModel implements SingleFixedChoicePage.OnNextPage{
    Context context;

    public WizardModel(Context context) {
        super(context);
        this.context = context;
    }

    @Override
    protected PageList onNewRootPageList() {
        return new PageList(new BranchPage(this, "Verhaltensregeln:", this).addBranch(
                "Brandfall",
                new ContentPage(this, "Entdecken"),
                new ContentPage(this, "Retten vor löschen!"),
                new ContentPage(this, "Fenster schließen"),
    //            new BranchPage(this, "Fluchtwege", this).addBranch(
    //                    "Neubau",
    //                    new ContentPage(this, "Neubau")
    //            ).addBranch(
    //                    "5er Räume",
    //                    new ContentPage(this, "5er Räume")
    //            ).addBranch(
    //                    "NwT Räume",
    //                    new ContentPage(this, "NwT Räume")
    //            ),
                new ContentPage(this, "Fluchtweg"),
                new ContentPage(this, "Flucht unmöglich?"),
                new ContentPage(this, "Vermisste?"))
        .addBranch("Amoklauf", new ContentPage(this, "Notruf"),
                new ContentPage(this, "Verhalten"))
        .addBranch("Chemieunfall",
                new ContentPage(this, "Gefahrensymbole"),
                new ContentPage(this, "Warnhinweise"))
    //    .addBranch("Sonstiger Unfall", new BranchPage(this, "Unfallart", this)
    //            .addBranch("Verletzung eines Schülers",
    //                    new ContentPage(this, "WIP"))
    //            .addBranch("WIP")
    //            .setRequired(true))
        .addBranch("Erste Hilfe", new ContentPage(this, "Erster Blick"),
                new ContentPage(this, "Sicherheit"),
                new ContentPage(this, "Retten aus Gefahr"),
                new ContentPage(this, "Erste Hilfe"),
                new ContentPage(this, "Notruf tätigen"),
                new ContentPage(this, "Weitere Maßnahmen"),
                new ContentPage(this, "Notarzt"))
        .addBranch("Notruf tätigen")
        .setRequired(true));
    }

    @Override
    public void onNextPage() {
        if (ActivityBehavior.mEditingAfterReview) {
            ActivityBehavior.mPager.setCurrentItem(ActivityBehavior.mPagerAdapter.getCount() - 1);
        } else {
            ActivityBehavior.mPager.setCurrentItem(ActivityBehavior.mPager.getCurrentItem() + 1);
        }
    }
}
