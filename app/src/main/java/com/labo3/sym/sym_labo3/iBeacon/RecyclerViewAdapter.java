package com.labo3.sym.sym_labo3.iBeacon;

import android.support.annotation.NonNull;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import com.labo3.sym.sym_labo3.R;
import org.altbeacon.beacon.Beacon;

import java.util.ArrayList;
import java.util.Collection;

/**
 * Adapter utilisé pour gérer la liste des posts d'un auteur dans le fragment GraphQLSendFragment affiché lorsque l'utilisateur sélectionne "GraphQL Object Transmission"
 * dans le menu ou sur le fragment "Home".
 */
class RecyclerViewAdapter extends RecyclerView.Adapter<IBeaconViewHolder> {
	private Collection<Beacon> listIBeacons;
	
	/**
	 * Contructeur
	 */
	RecyclerViewAdapter() {
		listIBeacons = new ArrayList<>();
	}
	
	/**
	 * Affiche les posts contenus dans la liste reçue en paramètre.
	 *
	 * @param listIBeacons, un tableau contenant les Post à afficher.
	 */
	void setListIBeacons(Collection<Beacon> listIBeacons) {
		this.listIBeacons = listIBeacons;
		// on notifie l'adapter que ses données ont été modifiées afin qu'il mette à jour la RecyclerView et affiche les nouvelles données.
		notifyDataSetChanged();
	}
	
	@NonNull
	@Override
	public IBeaconViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
		View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.activity_ibeacon_recyclerview_card, viewGroup, false);
		return new IBeaconViewHolder(view);
	}
	
	@Override
	public void onBindViewHolder(@NonNull IBeaconViewHolder IBeaconViewHolder, int i) {
		Object[] beacons = listIBeacons.toArray();
		Beacon iBeacon = (Beacon) beacons[i];
		IBeaconViewHolder.bind(iBeacon);
	}
	
	@Override
	public int getItemCount() {
		return listIBeacons.size();
	}
}
