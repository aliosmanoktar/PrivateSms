/*
 * Copyright (c) 2019. Ali Osman OKTAR
 * aliosmanoktar@gmail.com
 */

package com.aliosman.privatesms.Fragment;

import android.os.Bundle;
import android.support.design.widget.BottomSheetDialogFragment;
import android.text.format.DateUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import com.aliosman.privatesms.AppContents;
import com.aliosman.privatesms.Model.Message;
import com.aliosman.privatesms.R;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

public class DialogMessageInfo extends BottomSheetDialogFragment {
    private Message message;
    private TextView sendDate, deliveredDate, number;
    private Button btn_OK;
    private String TAG = getClass().getName();
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.dialog_message_info, container, false);
        message = (Message) getArguments().getSerializable(AppContents.MessageInfoDialog_Message);
        sendDate = view.findViewById(R.id.dialog_message_info_sendDate);
        number = view.findViewById(R.id.dialog_message_info_number);
        deliveredDate = view.findViewById(R.id.dialog_message_info_deliveredDate);
        sendDate.setText(getString(R.string.dialog_message_info_sentDate, getTime(message.getSendDate())));
        deliveredDate.setText(getString(R.string.dialog_message_info_deliveredDate, getTime(message.getDeliveredDate())));
        number.setText(getString(R.string.dialog_message_info_number, message.getContact().getNumber()));
        btn_OK = view.findViewById(R.id.dialog_message_info_ok);
        btn_OK.setOnClickListener(v -> dismiss());
        setStyle(R.style.AppBottomSheetDialogTheme, getTheme());
        return view;
    }

    private String getTime(long time) {
        if (time == 0)
            return "    --";
        DateFormat dateFormat =
                DateUtils.isToday(time) ?
                        new SimpleDateFormat("HH:mm") :
                        (IsYear(time) ?
                                new SimpleDateFormat("HH:mm dd/MM") :
                                new SimpleDateFormat("HH:mm dd/MM/yyyy"));
        return dateFormat.format(new Date(time));
    }

    private boolean IsYear(long time) {
        Date date = new Date(time);
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(date);
        return calendar.get(Calendar.YEAR) == Calendar.getInstance().get(Calendar.YEAR);
    }

}
