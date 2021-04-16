package com.cl.mvvm.binding.viewadapter.checkbox;

import android.widget.CheckBox;

import androidx.databinding.BindingAdapter;

import com.cl.mvvm.binding.command.BindingCommand;


/**
 * CheckBox 的 ViewAdapter
 */
public class ViewAdapter {
    /**
     * @param bindingCommand //绑定监听
     */
    @SuppressWarnings("unchecked")
    @BindingAdapter(value = {"onCheckedChanged"}, requireAll = false)
    public static void setCheckedChanged(final CheckBox checkBox, final BindingCommand<Boolean> bindingCommand) {
        checkBox.setOnCheckedChangeListener((compoundButton, b) -> bindingCommand.execute(b));
    }
}
