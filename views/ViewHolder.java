package views;

import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.GridLayout;
import android.widget.TextView;

import com.github.zagum.switchicon.SwitchIconView;

import volkovmedia.perfo.studentsalarm.R;

class ViewHolder extends RecyclerView.ViewHolder{

    TextView vName, vTime, vCondition;
    GridLayout vDays, vWeeks;
    SwitchIconView vSwitch;
    CardView vBasis;

    ViewHolder(View itemView) {
        super(itemView);

        vBasis = (CardView) itemView;

        vName = (TextView) itemView.findViewById(R.id.alarm_name);
        vCondition = (TextView) itemView.findViewById(R.id.alarm_condition);
        vTime = (TextView) itemView.findViewById(R.id.alarm_time);

        vDays = (GridLayout) itemView.findViewById(R.id.alarm_days);
        vWeeks = (GridLayout) itemView.findViewById(R.id.alarm_weeks);

        vSwitch = (SwitchIconView) itemView.findViewById(R.id.alarm_switch);
    }
}