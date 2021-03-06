package com.charlessodre.apps.gerenciadorfinanceiroisis.actCadastros;

import android.graphics.Color;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import com.charlessodre.apps.gerenciadorfinanceiroisis.R;
import com.charlessodre.apps.gerenciadorfinanceiroisis.appHelper.ColorHelper;
import com.charlessodre.apps.gerenciadorfinanceiroisis.fragmentos.frgIconeCor;
import com.charlessodre.apps.gerenciadorfinanceiroisis.fragmentos.frgOrdemExibicaoCor;
import com.charlessodre.apps.gerenciadorfinanceiroisis.fragmentos.frgConfirmaExclusaoDialog;
import com.charlessodre.apps.gerenciadorfinanceiroisis.fragmentos.frgConfirmacaoDialog;
import com.charlessodre.apps.gerenciadorfinanceiroisis.fragmentos.frgLancamentosDialog;
import com.charlessodre.apps.gerenciadorfinanceiroisis.util.ActionBarHelper;
import com.charlessodre.apps.gerenciadorfinanceiroisis.util.FragmentHelper;
import com.charlessodre.apps.gerenciadorfinanceiroisis.util.StringUtils;

public abstract class actBaseCadastros extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    protected abstract void inicializaObjetos();

    protected void setMenuHome(String titulo)
    {
        ActionBarHelper.menuHome(getSupportActionBar(), titulo);
    }

    protected void setMenuCancel(String titulo)
    {
        ActionBarHelper.menuCancel(getSupportActionBar(), titulo);
    }
    protected void setColorStatusBar(int color)
    {
        ActionBarHelper.setStatusBarColor(this.getWindow(), ColorHelper.getColor(this, color));

    }

    protected void setColorActionBar(int color)
    {
        ActionBarHelper.setBackgroundColor(getSupportActionBar(), Color.parseColor(getResources().getString(color)));
    }



    protected int getNumOrdemExibicao() {
        int numOrdem = 1;

        if (!StringUtils.isNullOrEmpty(FragmentHelper.getTextEditTextFragment(getSupportFragmentManager(), frgOrdemExibicaoCor.NOME_FRAGMENTO, R.id.edtOrdemExibicao)))
            numOrdem = Integer.parseInt(FragmentHelper.getTextEditTextFragment(getSupportFragmentManager(), frgOrdemExibicaoCor.NOME_FRAGMENTO, R.id.edtOrdemExibicao));

        return numOrdem;
    }

    protected int getNumCor() {
        return FragmentHelper.getSpinnerSelectedItemID(getSupportFragmentManager(), frgOrdemExibicaoCor.NOME_FRAGMENTO, R.id.spnCor);
    }

    protected int getNumCorIcone() {
        return FragmentHelper.getSpinnerSelectedItemID(getSupportFragmentManager(), frgIconeCor.NOME_FRAGMENTO, R.id.spnCor);
    }

    protected int getNumIcone() {
        return FragmentHelper.getSpinnerSelectedItemID(getSupportFragmentManager(), frgIconeCor.NOME_FRAGMENTO, R.id.spnIcone);
    }

    protected void exibePopUpLancamento(int tipoLancamento, int tipoMensagem) {

        Bundle argument = new Bundle();

        argument.putInt(frgLancamentosDialog.PARAM_TIPO_LANCAMENTO, tipoLancamento);
        argument.putInt(frgLancamentosDialog.PARAM_TIPO_MSG, tipoMensagem);

        frgLancamentosDialog dlg = new frgLancamentosDialog();

        dlg.setArguments(argument);

        dlg.show(getSupportFragmentManager(), frgLancamentosDialog.NOME_FRAGMENTO);

    }

    protected void adicionaFragOrdemExibicaoCor(int ordemExibicao, int numeroCor, int containerView) {
        Bundle argument = new Bundle();

        argument.putInt(frgOrdemExibicaoCor.PARAM_ORDEM_EXIBICAO, ordemExibicao);
        argument.putInt(frgOrdemExibicaoCor.PARAM_COR, numeroCor);

        FragmentHelper.addFragment(getSupportFragmentManager(), new frgOrdemExibicaoCor(), argument, frgOrdemExibicaoCor.NOME_FRAGMENTO, containerView);

    }

    protected void adicionaFragIconeCor(int numeroIcone, int numeroCor, int containerView) {
        Bundle argument = new Bundle();

        argument.putInt(frgIconeCor.PARAM_ICONE, numeroIcone);
        argument.putInt(frgIconeCor.PARAM_COR, numeroCor);

        FragmentHelper.addFragment(getSupportFragmentManager(), new frgIconeCor(), argument, frgIconeCor.NOME_FRAGMENTO, containerView);

    }

    protected void exibePopUpConfirmacaoDialog(String mensagem) {

        frgConfirmacaoDialog dlg = new frgConfirmacaoDialog();

        Bundle bundle = new Bundle();

        bundle.putString(frgConfirmacaoDialog.PARAM_MSG, mensagem);

        dlg.setArguments(bundle);
        dlg.show(getSupportFragmentManager(), frgConfirmacaoDialog.NOME_FRAGMENTO);
    }

    protected void exibeExclusaoDialog() {
        frgConfirmaExclusaoDialog dlg = new frgConfirmaExclusaoDialog();
        dlg.show(getSupportFragmentManager(), frgConfirmaExclusaoDialog.NOME_FRAGMENTO);
    }


}
