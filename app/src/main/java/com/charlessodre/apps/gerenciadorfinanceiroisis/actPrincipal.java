package com.charlessodre.apps.gerenciadorfinanceiroisis;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.view.View;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ArrayAdapter;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;


import com.charlessodre.apps.gerenciadorfinanceiroisis.actConsultas.actBaseListas;
import com.charlessodre.apps.gerenciadorfinanceiroisis.actConsultas.actCartaoCredito;
import com.charlessodre.apps.gerenciadorfinanceiroisis.actConsultas.actCategoriaDespesa;
import com.charlessodre.apps.gerenciadorfinanceiroisis.actConsultas.actCategoriaReceita;
import com.charlessodre.apps.gerenciadorfinanceiroisis.actConsultas.actConta;
import com.charlessodre.apps.gerenciadorfinanceiroisis.actConsultas.actDespesa;
import com.charlessodre.apps.gerenciadorfinanceiroisis.actConsultas.actListaSMS;
import com.charlessodre.apps.gerenciadorfinanceiroisis.actConsultas.actMovimentos;
import com.charlessodre.apps.gerenciadorfinanceiroisis.actConsultas.actReceita;
import com.charlessodre.apps.gerenciadorfinanceiroisis.actConsultas.actRegraImportacaoSMS;
import com.charlessodre.apps.gerenciadorfinanceiroisis.actConsultas.actTransferencia;
import com.charlessodre.apps.gerenciadorfinanceiroisis.fragmentos.frgBotaoAddTransacao;
import com.charlessodre.apps.gerenciadorfinanceiroisis.fragmentos.frgResumo;
import com.charlessodre.apps.gerenciadorfinanceiroisis.util.FragmentHelper;
import com.charlessodre.apps.gerenciadorfinanceiroisis.util.MessageBoxHelper;
import com.charlessodre.apps.gerenciadorfinanceiroisis.util.PermissionsUtil;
import java.util.ArrayList;
import java.util.List;

import com.charlessodre.apps.gerenciadorfinanceiroisis.fragmentos.frgGrafResumoReceitaDespesa;


public class actPrincipal extends actBaseListas
        implements NavigationView.OnNavigationItemSelectedListener, View.OnClickListener {

    //Objetos tela
    private ImageButton btnEsquerda;
    private ImageButton btnDireita;
    private TextView txtNomeMesResumo;

    //Fragmentos
    private frgResumo fragmentoResumo;
    private frgGrafResumoReceitaDespesa frgGrafResumoReceitaDespesaTotal;
    private frgGrafResumoReceitaDespesa frgGrafResumoReceitaDespesaConfirmadas;
    private frgBotaoAddTransacao fragmentoBotaoAdd;

    //Atributos


    //Eventos
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.act_principal);

        PermissionsUtil.askPermissions(this);
        //Carregmento inicial
        this.inicializaObjetos();
        super.setAddMesCalendar(0);
        this.setNomeMes();

    }


    @Override
    public void onRequestPermissionsResult(int requestCode, String permissions[], int[] grantResults) {

        switch (requestCode) {
            case PermissionsUtil.PERMISSION_ALL: {

                if (grantResults.length > 0) {

                    List<Integer> indexesOfPermissionsNeededToShow = new ArrayList<>();

                    for (int i = 0; i < permissions.length; ++i) {
                        if (ActivityCompat.shouldShowRequestPermissionRationale(this, permissions[i])) {
                            indexesOfPermissionsNeededToShow.add(i);
                        }
                    }

                    int size = indexesOfPermissionsNeededToShow.size();
                    if (size != 0) {
                        int i = 0;
                        boolean isPermissionGranted = true;

                        while (i < size && isPermissionGranted) {
                            isPermissionGranted = grantResults[indexesOfPermissionsNeededToShow.get(i)]
                                    == PackageManager.PERMISSION_GRANTED;
                            i++;
                        }

                        if (!isPermissionGranted) {

                            DialogInterface.OnClickListener okListener = new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialogInterface, int i) {
                                    verificaPermissoes();
                                }
                            };

                            MessageBoxHelper.show(this, this.getString(R.string.title_permissao_obrigatoria), this.getString(R.string.msg_permissoes_obrigatorias), 0, okListener);

                        }
                    }
                }
            }
        }
    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.act_principal, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        this.atualizaFragmentos();
    }

    @Override
    public void onClick(View v) {

        switch (v.getId()) {
            case R.id.btnEsquerdaResumo:
                super.setAddMesCalendar(-1);
                this.setNomeMes();
                this.atualizaFragmentos();

                break;
            case R.id.btnDireitaResumo:
                super.setAddMesCalendar(1);
                this.setNomeMes();
                this.atualizaFragmentos();
                break;

        }


    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        if (id == R.id.nav_categoria_despesa) {
            // Handle the camera action

            Intent it = new Intent(this, actCategoriaDespesa.class);
            startActivityForResult(it, 0);


        } else if (id == R.id.nav_categoria_receita) {


            Intent it = new Intent(this, actCategoriaReceita.class);
            startActivityForResult(it, 0);

        } else if (id == R.id.nav_conta) {
            Intent it = new Intent(this, actConta.class);
            startActivityForResult(it, 0);

        } else if (id == R.id.nav_receita) {
            Intent it = new Intent(this, actReceita.class);
            startActivityForResult(it, 0);

        } else if (id == R.id.nav_despesa) {
            Intent it = new Intent(this, actDespesa.class);
            startActivityForResult(it, 0);

        } else if (id == R.id.nav_transferencia) {

            Intent it = new Intent(this, actTransferencia.class);
            startActivityForResult(it, 0);

        } else if (id == R.id.nav_cartao_credito) {

            Intent it = new Intent(this, actCartaoCredito.class);
            startActivityForResult(it, 0);

        } else if (id == R.id.nav_movimento) {

            Intent it = new Intent(this, actMovimentos.class);
            startActivityForResult(it, 0);

        } else if (id == R.id.nav_import_sms) {

            Intent it = new Intent(this, actListaSMS.class);
            startActivityForResult(it, 0);

        } else if (id == R.id.nav_cad_regra_sms) {

            Intent it = new Intent(this, actRegraImportacaoSMS.class);
            startActivityForResult(it, 0);
        } else if (id == R.id.nav_send) {


            //Intent it = new Intent(this, actResumoConta1.class);
            // startActivityForResult(it, 0);
            /*//exemplo_lista_single();
            verificaPermissoes();
            EnviarSMS sms = new EnviarSMS();

            sms.Enviar("+5521988548894", "Teste envio de mensagem pelo android. Feito por Charles" + DateUtils.getCurrentDatetime().toString());
            sms.Enviar("+5521964339672", "Teste envio de mensagem pelo android. Feito por Charles" + DateUtils.getCurrentDatetime().toString());
            ToastHelper.showToastLong(this, "SMS Enviado com sucesso!");
            */

        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }


    //Métodos

    private void verificaPermissoes() {
        PermissionsUtil.askPermissions(this);
    }


    @Override
    protected void inicializaObjetos() {

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);

        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.setDrawerListener(toggle);
        toggle.syncState();

        // super.setMenuHome(this.getString(R.string.title_principal));
        super.setColorStatusBar(R.color.corTelaPrincipal);


        this.btnDireita = (ImageButton) findViewById(R.id.btnDireitaResumo);
        this.btnEsquerda = (ImageButton) findViewById(R.id.btnEsquerdaResumo);

        this.btnDireita.setOnClickListener(this);
        this.btnEsquerda.setOnClickListener(this);

        this.txtNomeMesResumo = (TextView) this.findViewById(R.id.txtNomeMesResumo);

        //Adiciona o Fragmento de Resumo
        this.adicionaFragResumo();

        //Adiciona o Fragmento Grafico Total
        this.adicionaFragGrafReceitasDespesaTotal();

        //Adiciona o Fragmento Grafico Confirmadas
        this.adicionaFragGrafReceitasDespesaConfirmadas();

        //Adiciona o Fragmento Botões Add Transação.
        this.adicionaFragBotaoAdd();


    }

    private void adicionaFragResumo() {


        this.fragmentoResumo = (frgResumo) FragmentHelper.findFragmentByTag(getSupportFragmentManager(), frgResumo.NOME_FRAGMENTO);

        //Verifica se o fragmento já foi adicionado.
        if (this.fragmentoResumo == null) {
            this.fragmentoResumo = frgResumo.newInstance(super.getAnoMes());

            Bundle argument = new Bundle();

            FragmentHelper.addFragment(getSupportFragmentManager(), this.fragmentoResumo, frgResumo.NOME_FRAGMENTO, R.id.frag_container_1);
        }
    }

    private void adicionaFragGrafReceitasDespesaTotal() {

        this.frgGrafResumoReceitaDespesaTotal = (frgGrafResumoReceitaDespesa) FragmentHelper.findFragmentByTag(getSupportFragmentManager(), frgGrafResumoReceitaDespesa.NOME_FRAGMENTO);

        //Verifica se o fragmento já foi adicionado.
        if (this.frgGrafResumoReceitaDespesaTotal == null) {
            this.frgGrafResumoReceitaDespesaTotal = frgGrafResumoReceitaDespesa.newInstance(super.getAnoMes(), false);

            FragmentHelper.addFragment(getSupportFragmentManager(), this.frgGrafResumoReceitaDespesaTotal, frgGrafResumoReceitaDespesa.NOME_FRAGMENTO, R.id.frag_container_2);

        }
    }

    private void adicionaFragGrafReceitasDespesaConfirmadas() {

        this.frgGrafResumoReceitaDespesaConfirmadas = (frgGrafResumoReceitaDespesa) FragmentHelper.findFragmentByTag(getSupportFragmentManager(), frgGrafResumoReceitaDespesa.NOME_FRAGMENTO);

        //Verifica se o fragmento já foi adicionado.
        if (this.frgGrafResumoReceitaDespesaConfirmadas == null) {
            this.frgGrafResumoReceitaDespesaConfirmadas = frgGrafResumoReceitaDespesa.newInstance(super.getAnoMes(),true);

            FragmentHelper.addFragment(getSupportFragmentManager(), this.frgGrafResumoReceitaDespesaConfirmadas, frgGrafResumoReceitaDespesa.NOME_FRAGMENTO, R.id.frag_container_3);

        }
    }

    private void adicionaFragBotaoAdd() {


        this.fragmentoBotaoAdd = (frgBotaoAddTransacao) FragmentHelper.findFragmentByTag(getSupportFragmentManager(), fragmentoBotaoAdd.NOME_FRAGMENTO);

        //Verifica se o fragmento já foi adicionado.
        if (this.fragmentoBotaoAdd == null) {
            this.fragmentoBotaoAdd = fragmentoBotaoAdd.newInstance();

            Bundle argument = new Bundle();

            FragmentHelper.addFragment(getSupportFragmentManager(), this.fragmentoBotaoAdd, fragmentoBotaoAdd.NOME_FRAGMENTO, R.id.frag_container_botao_add_princ);
        }
    }


    private void setNomeMes() {

        this.txtNomeMesResumo.setText(super.getNomeMesFormatado());
    }

    private void atualizaFragmentos() {


        FragmentHelper.removeFragmentWithStateLoss(this.getSupportFragmentManager(), this.fragmentoResumo);
        FragmentHelper.removeFragmentWithStateLoss(this.getSupportFragmentManager(), this.frgGrafResumoReceitaDespesaTotal);
        FragmentHelper.removeFragmentWithStateLoss(this.getSupportFragmentManager(), this.frgGrafResumoReceitaDespesaConfirmadas);

        this.fragmentoResumo = frgResumo.newInstance(super.getAnoMes());
        this.frgGrafResumoReceitaDespesaTotal = frgGrafResumoReceitaDespesa.newInstance(super.getAnoMes(), false);
        this.frgGrafResumoReceitaDespesaConfirmadas = frgGrafResumoReceitaDespesa.newInstance(super.getAnoMes(), true);

        FragmentHelper.replaceFragmentWithStateLoss(this.getSupportFragmentManager(), this.fragmentoResumo, R.id.frag_container_1);
        FragmentHelper.replaceFragmentWithStateLoss(this.getSupportFragmentManager(), this.frgGrafResumoReceitaDespesaTotal, R.id.frag_container_2);
        FragmentHelper.replaceFragmentWithStateLoss(this.getSupportFragmentManager(), this.frgGrafResumoReceitaDespesaConfirmadas, R.id.frag_container_3);



    }


    //TESTE
    private AlertDialog alerta;

    private void exemplo_lista_single() {
        //Lista de itens
        ArrayList<String> itens = new ArrayList<String>();
        itens.add("Ruim");
        itens.add("Mediano");
        itens.add("Bom");
        itens.add("Ótimo");

        //adapter utilizando um layout customizado (TextView)
        ArrayAdapter adapter = new ArrayAdapter(this, R.layout.item_alerta, itens);

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Qualifique este software:");
        //define o diálogo como uma lista, passa o adapter.
        builder.setSingleChoiceItems(adapter, 0, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface arg0, int arg1) {
                Toast.makeText(actPrincipal.this, "posição selecionada=" + arg1, Toast.LENGTH_SHORT).show();
                alerta.dismiss();
            }
        });

        alerta = builder.create();
        alerta.show();
    }

}
