package com.charlessodre.apps.gerenciadorfinanceiroisis.actCadastros;

import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;

import com.charlessodre.apps.gerenciadorfinanceiroisis.R;
import com.charlessodre.apps.gerenciadorfinanceiroisis.appHelper.AdapterSubCategoriaDespesa;
import com.charlessodre.apps.gerenciadorfinanceiroisis.dominio.entidades.SubCategoriaDespesa;
import com.charlessodre.apps.gerenciadorfinanceiroisis.dominio.repositorios.RepositorioSubCategoriaDespesa;
import com.charlessodre.apps.gerenciadorfinanceiroisis.fragmentos.frgConfirmacaoDialog;
import com.charlessodre.apps.gerenciadorfinanceiroisis.fragmentos.frgLancamentosDialog;
import com.charlessodre.apps.gerenciadorfinanceiroisis.util.ActionBarHelper;
import com.charlessodre.apps.gerenciadorfinanceiroisis.appHelper.AdapterCategoriaDespesa;
import com.charlessodre.apps.gerenciadorfinanceiroisis.appHelper.AdapterConta;
import com.charlessodre.apps.gerenciadorfinanceiroisis.util.ArrayAdapterHelper;
import com.charlessodre.apps.gerenciadorfinanceiroisis.appHelper.ColorHelper;
import com.charlessodre.apps.gerenciadorfinanceiroisis.appHelper.Constantes;
import com.charlessodre.apps.gerenciadorfinanceiroisis.util.DateListenerShow;
import com.charlessodre.apps.gerenciadorfinanceiroisis.util.MessageBoxHelper;
import com.charlessodre.apps.gerenciadorfinanceiroisis.util.TextWatcherPay;
import com.charlessodre.apps.gerenciadorfinanceiroisis.util.ToastHelper;
import com.charlessodre.apps.gerenciadorfinanceiroisis.dominio.entidades.CategoriaDespesa;
import com.charlessodre.apps.gerenciadorfinanceiroisis.dominio.entidades.Conta;
import com.charlessodre.apps.gerenciadorfinanceiroisis.dominio.entidades.Despesa;
import com.charlessodre.apps.gerenciadorfinanceiroisis.dominio.entidades.TipoRepeticao;
import com.charlessodre.apps.gerenciadorfinanceiroisis.dominio.repositorios.RepositorioCategoriaDespesa;
import com.charlessodre.apps.gerenciadorfinanceiroisis.dominio.repositorios.RepositorioConta;
import com.charlessodre.apps.gerenciadorfinanceiroisis.dominio.repositorios.RepositorioDespesa;
import com.charlessodre.apps.gerenciadorfinanceiroisis.util.DateUtils;
import com.charlessodre.apps.gerenciadorfinanceiroisis.util.NumberUtis;
import com.charlessodre.apps.gerenciadorfinanceiroisis.util.StringUtils;

import java.util.Date;

public class actCadDespesa extends actBaseCadastros implements CompoundButton.OnCheckedChangeListener, frgLancamentosDialog.onDialogClick, frgConfirmacaoDialog.onDialogClick, Spinner.OnItemSelectedListener {

    //Objetos Tela
    private EditText edtNome;
    private Spinner spnCategoriaDespesa;
    private Spinner spnSubCategoriaDespesa;
    private Spinner spnContaDespesa;
    private Spinner spnTipoRepeticao;
    private EditText edtDataDespesa;
    private EditText edtDataPagamento;
    private EditText edtValorDespesa;
    private EditText edtTotalRepeticao;
    private CheckBox cbxDespesaPaga;
    private CheckBox cbxRepetir;
    private CheckBox cbxFixa;
    private TextWatcherPay textWatcher;
    private TextView txtParcelas;

    private LinearLayout lnlDetalhePagamento;

    //Atributos
    private Despesa despesa;
    private RepositorioDespesa repositorioDespesa;
    private DateListenerShow dataDespesaListenerShow;
    private DateListenerShow dataPagamentoDespesaListenerShow;
    private AdapterConta adapterConta;
    private AdapterCategoriaDespesa adapterCategoriaDespesa;
    private AdapterSubCategoriaDespesa adapterSubCategoriaDespesa;
    private Date dataDespesa;
    private Date dataPagamentoDespesa;

    //Contantes
    public static final String PARAM_DESPESA = "DESPESA";

    //Eventos
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.act_cad_despesa);

        inicializaObjetos();
        carregaSpinnerConta();
        carregaSpinnerCategoriaDespesa();
        carregaSpinnerTipoRepeticao();

        preencheDados();

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_cadastro, menu);

        if (this.despesa.getId() == 0) {
            menu.findItem(R.id.menu_excluir).setVisible(false);
        }

        return super.onCreateOptionsMenu(menu);
    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        switch (item.getItemId()) {

            case android.R.id.home:

                this.finish();
                break;

            case R.id.menu_salvar:

                if (validaCamposTela()) {

                    this.getTipoSalvamento();
                }

                break;

            case R.id.menu_excluir:

                this.getTipoExclusao();
                break;

            default:
                break;
        }

        return true;
    }

    @Override
    public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {

        if (buttonView.getId() == R.id.cbxFixa) {
            if (this.cbxFixa.isChecked()) {
                this.cbxRepetir.setChecked(false);
                this.edtTotalRepeticao.setEnabled(false);
                this.spnTipoRepeticao.setEnabled(false);
            }

        } else if (buttonView.getId() == R.id.cbxRepetir) {
            if (this.cbxRepetir.isChecked()) {
                this.cbxFixa.setChecked(false);
                this.edtTotalRepeticao.setEnabled(true);
                this.spnTipoRepeticao.setEnabled(true);

            }
        } else if (buttonView.getId() == R.id.cbxDespesaPaga) {
            if (this.cbxDespesaPaga.isChecked())
                this.lnlDetalhePagamento.setVisibility(View.VISIBLE);
            else
                this.lnlDetalhePagamento.setVisibility(View.GONE);
        }
    }

    @Override
    public void onDialogClick(frgLancamentosDialog dialog, int opcaoSelecionada, int tipoMensagem) {

        if (tipoMensagem == Constantes.TipoMensagem.EXCLUSAO) {
            if (opcaoSelecionada == Constantes.OpcaoExclusaoAlteracao.ATUAL) {
                this.excluiAtual();

            } else if (opcaoSelecionada == Constantes.OpcaoExclusaoAlteracao.PENDENTES) {
                this.excluiPendentes();

            } else if (opcaoSelecionada == Constantes.OpcaoExclusaoAlteracao.TODAS) {
                this.excluiTodas();
            } else if (opcaoSelecionada == Constantes.OpcaoExclusaoAlteracao.CANCELAR) {
                dialog.dismiss();
            }
        } else if (tipoMensagem == Constantes.TipoMensagem.ALTERACAO) {

            if (opcaoSelecionada == Constantes.OpcaoExclusaoAlteracao.ATUAL) {
                this.salvaAtual();

            } else if (opcaoSelecionada == Constantes.OpcaoExclusaoAlteracao.PENDENTES) {
                this.salvaProximas();

            } else if (opcaoSelecionada == Constantes.OpcaoExclusaoAlteracao.TODAS) {
                this.salvaTodas();
            } else if (opcaoSelecionada == Constantes.OpcaoExclusaoAlteracao.CANCELAR) {
                dialog.dismiss();
            }
        }
    }

    @Override
    public void onDialogClick(frgConfirmacaoDialog dialog) {
        this.excluiAtual();

    }

    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
        long idCategoria = ((CategoriaDespesa) parent.getSelectedItem()).getId();
        this.carregaSpinnerSubCategoriaDespesa(idCategoria);

        this.spnSubCategoriaDespesa.setSelection(this.adapterSubCategoriaDespesa.getIndexFromElement(this.despesa.getSubCategoriaDespesa().getId()));

    }

    @Override
    public void onNothingSelected(AdapterView<?> parent) {

    }

    //Métodos
    @Override
    protected void inicializaObjetos() {
        this.edtNome = (EditText) findViewById(R.id.edtNomeDespesa);
        this.spnCategoriaDespesa = (Spinner) findViewById(R.id.spnCategoriaDespesa);
        this.spnContaDespesa = (Spinner) findViewById(R.id.spnContaDespesa);
        this.spnSubCategoriaDespesa = (Spinner) findViewById(R.id.spnSubCategoriaDespesa);

        this.spnCategoriaDespesa.setOnItemSelectedListener(this);

        this.lnlDetalhePagamento = (LinearLayout) findViewById(R.id.lnlDetalhePagamento);


        this.spnTipoRepeticao = (Spinner) findViewById(R.id.spnTipoRepeticao);
        this.edtDataDespesa = (EditText) findViewById(R.id.edtDataDespesa);
        this.edtDataPagamento = (EditText) findViewById(R.id.edtDataPagamento);
        this.edtValorDespesa = (EditText) findViewById(R.id.edtValorDespesa);
        this.edtTotalRepeticao = (EditText) findViewById(R.id.edtTotalRepeticao);
        this.cbxDespesaPaga = (CheckBox) findViewById(R.id.cbxDespesaPaga);
        this.cbxFixa = (CheckBox) findViewById(R.id.cbxFixa);
        this.cbxRepetir = (CheckBox) findViewById(R.id.cbxRepetir);
        this.txtParcelas = (TextView) findViewById(R.id.txtParcelas);

        this.cbxFixa.setOnCheckedChangeListener(this);
        this.cbxRepetir.setOnCheckedChangeListener(this);
        this.cbxDespesaPaga.setOnCheckedChangeListener(this);

        this.spnTipoRepeticao.setEnabled(false);

        this.textWatcher = new TextWatcherPay(this.edtValorDespesa, "%.2f");
        this.edtValorDespesa.addTextChangedListener(this.textWatcher);

        ActionBarHelper.menuCancel(getSupportActionBar(), this.getString(R.string.lblDespesa));

        int corTela = ColorHelper.getColor(this, R.color.corTelaDespesas);

        ActionBarHelper.setBackgroundColor(getSupportActionBar(), corTela);

        ActionBarHelper.setStatusBarColor(this.getWindow(), corTela);

        this.dataDespesaListenerShow = new DateListenerShow(this, this.edtDataDespesa, false);
        this.dataPagamentoDespesaListenerShow = new DateListenerShow(this, this.edtDataPagamento, false);

        this.edtDataDespesa.setText(DateUtils.getCurrentDateShort());
        this.edtDataPagamento.setText(DateUtils.getCurrentDateShort());

    }


    private void carregaSpinnerConta() {
        RepositorioConta repositorioConta = new RepositorioConta(this);

        this.adapterConta = new AdapterConta(this, R.layout.item_conta_simples);

        this.adapterConta.addAll(repositorioConta.getAll());

        this.spnContaDespesa.setAdapter(this.adapterConta);
    }

    private void carregaSpinnerCategoriaDespesa() {
        RepositorioCategoriaDespesa repositorioCategoriaDespesa = new RepositorioCategoriaDespesa(this);

        this.adapterCategoriaDespesa = new AdapterCategoriaDespesa(this, R.layout.item_categoria);

        this.adapterCategoriaDespesa.addAll(repositorioCategoriaDespesa.getAll());

        this.spnCategoriaDespesa.setAdapter(this.adapterCategoriaDespesa);
    }

    private void carregaSpinnerSubCategoriaDespesa(Long idCategoriaDespesa) {

        RepositorioSubCategoriaDespesa repositorioSubCategoriaDespesa = new RepositorioSubCategoriaDespesa(this);

        this.adapterSubCategoriaDespesa = new AdapterSubCategoriaDespesa(this, R.layout.item_sub_categoria);

        this.adapterSubCategoriaDespesa.addAll(repositorioSubCategoriaDespesa.buscaPorIdCategoriaDespesa(idCategoriaDespesa));

        this.spnSubCategoriaDespesa.setAdapter(this.adapterSubCategoriaDespesa);
    }

    private void carregaSpinnerTipoRepeticao() {
        ArrayAdapter arrayAdapter = ArrayAdapterHelper.fillSpinnerString(this, this.spnTipoRepeticao);
        arrayAdapter.addAll(TipoRepeticao.getTipoRepeticao());
    }

    private void preencheDados() {
        Bundle bundle = getIntent().getExtras();

        if ((bundle != null) && (bundle.containsKey(actCadDespesa.PARAM_DESPESA))) {
            this.despesa = (Despesa) bundle.getSerializable(actCadDespesa.PARAM_DESPESA);

            this.edtNome.setText(this.despesa.getNome());

            this.edtValorDespesa.setText(NumberUtis.getFormartCurrency(this.despesa.getValor()));

            this.spnCategoriaDespesa.setSelection(this.adapterCategoriaDespesa.getIndexFromElement(this.despesa.getCategoriaDespesa().getId()));
            this.spnContaDespesa.setSelection(this.adapterConta.getIndexFromElement(this.despesa.getConta().getId()));


            this.edtDataDespesa.setText(DateUtils.dateToString(this.despesa.getDataDespesa()));

            if(this.despesa.isPaga())
                this.edtDataPagamento.setText(DateUtils.dateToString(this.despesa.getDataPagamento()));

            this.cbxDespesaPaga.setChecked(this.despesa.isPaga());


            if (this.despesa.getTotalRepeticao() > 0 || this.despesa.isFixa()) {

                this.spnTipoRepeticao.setSelection(this.despesa.getIdTipoRepeticao());
                this.edtTotalRepeticao.setText(String.valueOf(this.despesa.getTotalRepeticao()));
                this.cbxRepetir.setChecked(true);
                this.cbxFixa.setChecked(this.despesa.isFixa());

                if (!this.despesa.isFixa()) {
                    this.txtParcelas.setVisibility(View.VISIBLE);
                    this.txtParcelas.setText(this.despesa.getRepeticaoAtual() + "/" + this.despesa.getTotalRepeticao());
                    this.spnTipoRepeticao.setSelection(this.despesa.getIdTipoRepeticao());
                    this.edtDataDespesa.setEnabled(false);
                }
            }

            this.cbxRepetir.setEnabled(false);
            this.edtTotalRepeticao.setEnabled(false);
            this.spnTipoRepeticao.setEnabled(false);
            this.cbxFixa.setEnabled(false);

        } else {
            this.despesa = new Despesa();
        }
            }

    private boolean validaCamposTela() {
        boolean retorno = true;

        if (StringUtils.isNullOrEmpty(this.edtNome.getText().toString())) {
            this.edtNome.setError(this.getString(R.string.msg_preenchimento_obrigatorio));

            retorno = false;
        }

        if (this.textWatcher.getValueWithoutMask() <= 0) {
            this.edtValorDespesa.setError(this.getString(R.string.msg_valor_maior_zero));

            retorno = false;
        }

        if(this.spnContaDespesa.getCount() < 1)
        {
            MessageBoxHelper.show(this,"", this.getString(R.string.msg_cadastrar_conta));
            retorno = false;
        }

        return retorno;

    }

    private void getTipoSalvamento() {

        if (this.despesa.getTotalRepeticao() > 0 || this.despesa.isFixa()) {

            super.exibePopUpLancamento(Constantes.TipoLancamento.LANCAMENTO_RECEITA, Constantes.TipoMensagem.ALTERACAO);

        } else {
            this.salvaAtual();
        }

    }

    private void preencheDadosSalvar() {

        this.despesa.setNome(this.edtNome.getText().toString());

        this.despesa.setValor(this.textWatcher.getValueWithoutMask());

        this.dataDespesa = this.dataDespesaListenerShow.getDateListenerSelect().getDate();

        if (this.dataDespesa == null) {
            if(this.despesa.getDataDespesa()!=null)
                this.dataDespesa = this.despesa.getDataDespesa();
            else
                this.dataDespesa = DateUtils.getCurrentDatetime();
        }

        this.despesa.setDataDespesa(this.dataDespesa);
        this.despesa.setAnoMesDespesa(DateUtils.getYearAndMonth(this.dataDespesa));

        Conta conta = this.adapterConta.getItem(this.spnContaDespesa.getSelectedItemPosition());

        CategoriaDespesa categoriaDespesa = this.adapterCategoriaDespesa.getItem(this.spnCategoriaDespesa.getSelectedItemPosition());

        if (this.adapterSubCategoriaDespesa.getCount() > 0) {
            SubCategoriaDespesa subCategoriaDespesa = this.adapterSubCategoriaDespesa.getItem(this.spnSubCategoriaDespesa.getSelectedItemPosition());
            this.despesa.setSubCategoriaDespesa(subCategoriaDespesa);
        }

        this.despesa.setConta(conta);
        this.despesa.setCategoriaDespesa(categoriaDespesa);


        if (this.despesa.getId() == 0) {
            if (this.cbxRepetir.isChecked()) {

                int totalRepeticao = 1;

                if (!StringUtils.isNullOrEmpty(this.edtTotalRepeticao.getText().toString()))
                    totalRepeticao = Integer.parseInt(this.edtTotalRepeticao.getText().toString());

                this.despesa.setTotalRepeticao(totalRepeticao);
                this.despesa.setRepeticaoAtual(1);
                this.despesa.setIdTipoRepeticao(this.spnTipoRepeticao.getSelectedItemPosition());
            }

            this.despesa.setFixa(this.cbxFixa.isChecked());
        }

        if (this.cbxDespesaPaga.isChecked()) {

            this.despesa.setPaga(true);

            this.dataPagamentoDespesa = this.dataPagamentoDespesaListenerShow.getDateListenerSelect().getDate();

            if (this.dataPagamentoDespesa == null)
                this.dataPagamentoDespesa = DateUtils.getCurrentDatetime();

            this.despesa.setDataPagamento(this.dataPagamentoDespesa);
            this.despesa.setAnoMesPagamento(DateUtils.getYearAndMonth(this.dataPagamentoDespesa));


        } else if (!this.cbxDespesaPaga.isChecked() && this.despesa.getId() != 0 && this.despesa.getDataPagamento() != null) {

            this.despesa.setEstornaPagamento(true);
            this.despesa.setPaga(false);
            this.despesa.setDataPagamento(null);
            this.despesa.setAnoMesPagamento(null);

        }


        this.despesa.setOrdemExibicao(super.getNumOrdemExibicao());

    }

    private void salvaAtual() {

        this.preencheDadosSalvar();

        try {

            this.repositorioDespesa = new RepositorioDespesa(this);

            if (this.despesa.getId() == 0) {
                this.despesa.setDataInclusao(DateUtils.getCurrentDatetime());
                repositorioDespesa.insere(this.despesa);
            } else {
                this.despesa.setDataAlteracao(DateUtils.getCurrentDatetime());
                repositorioDespesa.altera(this.despesa);
            }

            ToastHelper.showToastShort(this, this.getString(R.string.msg_salvar_sucesso));
            this.finish();


        } catch (Exception ex) {

            MessageBoxHelper.showAlert(this, this.getResources().getString(R.string.lblErro), ex.getMessage());
        }

    }

    private void salvaProximas() {

        this.preencheDadosSalvar();

        try {

            this.repositorioDespesa = new RepositorioDespesa(this);

            this.despesa.setDataAlteracao(DateUtils.getCurrentDatetime());
            repositorioDespesa.alteraProximas(this.despesa);

            ToastHelper.showToastShort(this, this.getString(R.string.msg_salvar_sucesso));
            this.finish();


        } catch (Exception ex) {

            MessageBoxHelper.showAlert(this, this.getResources().getString(R.string.lblErro), ex.getMessage());
        }

    }

    private void salvaTodas() {

        this.preencheDadosSalvar();

        try {

            this.repositorioDespesa = new RepositorioDespesa(this);

            this.despesa.setDataAlteracao(DateUtils.getCurrentDatetime());
            repositorioDespesa.alteraTodas(this.despesa);

            ToastHelper.showToastShort(this, this.getString(R.string.msg_salvar_sucesso));
            this.finish();


        } catch (Exception ex) {

            MessageBoxHelper.showAlert(this, this.getResources().getString(R.string.lblErro), ex.getMessage());
        }
    }

    private void getTipoExclusao() {

        if (this.despesa.getTotalRepeticao() > 0 || this.despesa.isFixa()) {

            super.exibePopUpLancamento(Constantes.TipoLancamento.LANCAMENTO_RECEITA, Constantes.TipoMensagem.EXCLUSAO);

        } else {
            super.exibePopUpConfirmacaoDialog(this.getString(R.string.msg_excluir));

        }


    }

    private void excluiAtual() {

        try {

            this.repositorioDespesa = new RepositorioDespesa(this);

            this.repositorioDespesa.exclui(this.despesa);

            ToastHelper.showToastShort(this, this.getString(R.string.msg_excluir_sucesso));

            this.finish();

        } catch (Exception ex) {

            MessageBoxHelper.showAlert(this, this.getResources().getString(R.string.lblErro), ex.getMessage());
        }

    }

    private void excluiPendentes() {


        try {

            this.repositorioDespesa = new RepositorioDespesa(this);

            this.repositorioDespesa.excluiProximas(this.despesa);

            ToastHelper.showToastShort(this, this.getString(R.string.msg_excluir_sucesso));
            this.finish();

        } catch (Exception ex) {

            MessageBoxHelper.showAlert(this, this.getResources().getString(R.string.lblErro), ex.getMessage());
        }

    }

    private void excluiTodas() {

        try {

            this.repositorioDespesa = new RepositorioDespesa(this);

            this.repositorioDespesa.excluiTodas(this.despesa);
            ToastHelper.showToastShort(this, this.getString(R.string.msg_excluir_sucesso));
            this.finish();

        } catch (Exception ex) {

            MessageBoxHelper.showAlert(this, this.getResources().getString(R.string.lblErro), ex.getMessage());
        }

    }


}
