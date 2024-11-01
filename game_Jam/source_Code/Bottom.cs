using System.Collections;
using System.Collections.Generic;
using UnityEngine;

public class Bottom: MonoBehaviour
{


    private void OnTriggerEnter2D(Collider2D collision)
    {
        if (collision.CompareTag("Player"))
        {
            PlayerLife player = collision.GetComponent<PlayerLife>();
            if (player != null)
            {
                player.TouchSea();

            }
        }
    }
}
